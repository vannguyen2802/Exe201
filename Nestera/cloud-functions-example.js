// ============================================
// Firebase Cloud Function - Set Custom Claims
// ============================================
//
// Deploy this to Firebase Cloud Functions to automatically
// set user roles when they register or login
//

const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp();

const db = admin.firestore();

/**
 * Trigger: Khi user mới đăng ký qua Firebase Auth
 * Action: Set custom claims dựa trên role trong Firestore
 */
exports.setUserRole = functions.auth.user().onCreate(async (user) => {
  const uid = user.uid;
  const email = user.email;

  console.log(`New user created: ${uid}, ${email}`);

  try {
    // Kiểm tra trong collection chuTro
    const chuTroDoc = await db.collection("chuTro").doc(uid).get();
    if (chuTroDoc.exists) {
      const data = chuTroDoc.data();
      await admin.auth().setCustomUserClaims(uid, {
        role: "LANDLORD",
        approved: data.approved || 0,
        banned: data.banned || 0,
      });
      console.log(`Set role LANDLORD for user ${uid}`);
      return;
    }

    // Kiểm tra trong collection nguoiThue
    const nguoiThueDoc = await db.collection("nguoiThue").doc(uid).get();
    if (nguoiThueDoc.exists) {
      await admin.auth().setCustomUserClaims(uid, {
        role: "TENANT",
      });
      console.log(`Set role TENANT for user ${uid}`);
      return;
    }

    // Kiểm tra trong collection keToan
    const keToanDoc = await db.collection("keToan").doc(uid).get();
    if (keToanDoc.exists) {
      await admin.auth().setCustomUserClaims(uid, {
        role: "ACCOUNTANT",
      });
      console.log(`Set role ACCOUNTANT for user ${uid}`);
      return;
    }

    // Default: no role
    console.log(`No role found for user ${uid}`);
  } catch (error) {
    console.error("Error setting custom claims:", error);
  }
});

/**
 * HTTP Function: Manually set user role
 * Usage: POST /setUserRole
 * Body: { "uid": "user_id", "role": "LANDLORD|TENANT|ACCOUNTANT|ADMIN" }
 */
exports.setUserRoleManual = functions.https.onCall(async (data, context) => {
  // Only admin can call this function
  if (!context.auth || context.auth.token.role !== "ADMIN") {
    throw new functions.https.HttpsError(
      "permission-denied",
      "Only admins can set user roles"
    );
  }

  const { uid, role } = data;

  if (!uid || !role) {
    throw new functions.https.HttpsError(
      "invalid-argument",
      "Missing uid or role"
    );
  }

  const validRoles = ["LANDLORD", "TENANT", "ACCOUNTANT", "ADMIN"];
  if (!validRoles.includes(role)) {
    throw new functions.https.HttpsError(
      "invalid-argument",
      "Invalid role. Must be one of: " + validRoles.join(", ")
    );
  }

  try {
    await admin.auth().setCustomUserClaims(uid, { role });
    console.log(`Admin ${context.auth.uid} set role ${role} for user ${uid}`);
    return { success: true, message: `Role ${role} set for user ${uid}` };
  } catch (error) {
    console.error("Error setting custom claims:", error);
    throw new functions.https.HttpsError("internal", error.message);
  }
});

/**
 * Firestore Trigger: Khi ChuTro được approve/ban
 * Action: Update custom claims
 */
exports.onChuTroApprove = functions.firestore
  .document("chuTro/{chuTroId}")
  .onUpdate(async (change, context) => {
    const before = change.before.data();
    const after = change.after.data();
    const uid = context.params.chuTroId;

    // Nếu approved hoặc banned status thay đổi
    if (before.approved !== after.approved || before.banned !== after.banned) {
      try {
        await admin.auth().setCustomUserClaims(uid, {
          role: "LANDLORD",
          approved: after.approved || 0,
          banned: after.banned || 0,
        });
        console.log(
          `Updated claims for landlord ${uid}: approved=${after.approved}, banned=${after.banned}`
        );
      } catch (error) {
        console.error("Error updating custom claims:", error);
      }
    }
  });

/**
 * HTTP Function: Get user role (for debugging)
 * Usage: POST /getUserRole
 * Body: { "uid": "user_id" }
 */
exports.getUserRole = functions.https.onCall(async (data, context) => {
  // Must be authenticated
  if (!context.auth) {
    throw new functions.https.HttpsError(
      "unauthenticated",
      "Must be logged in"
    );
  }

  const { uid } = data;
  const targetUid = uid || context.auth.uid;

  // Only admin can check other users' roles
  if (targetUid !== context.auth.uid && context.auth.token.role !== "ADMIN") {
    throw new functions.https.HttpsError(
      "permission-denied",
      "Only admins can check other users roles"
    );
  }

  try {
    const user = await admin.auth().getUser(targetUid);
    return {
      uid: targetUid,
      email: user.email,
      customClaims: user.customClaims || {},
    };
  } catch (error) {
    console.error("Error getting user:", error);
    throw new functions.https.HttpsError("internal", error.message);
  }
});

// ============================================
// DEPLOYMENT INSTRUCTIONS
// ============================================
//
// 1. Install Firebase CLI:
//    npm install -g firebase-tools
//
// 2. Login to Firebase:
//    firebase login
//
// 3. Initialize Functions (if not already done):
//    firebase init functions
//    - Select JavaScript or TypeScript
//    - Install dependencies
//
// 4. Copy this code to functions/index.js
//
// 5. Install dependencies:
//    cd functions
//    npm install firebase-functions firebase-admin
//
// 6. Deploy to Firebase:
//    firebase deploy --only functions
//
// 7. Test functions:
//    - Check Firebase Console > Functions
//    - Monitor logs: firebase functions:log
//
