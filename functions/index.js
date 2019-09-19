const functions = require('firebase-functions');
 
const admin = require('firebase-admin');
admin.initializeApp();
 
exports.notifyNewMessage = functions.firestore
    .document('ChatRooms/{chatRoomId}/messages/{messageId}')
    .onCreate((docSnapshot, context) => {
        const message = docSnapshot.data();
        const senderId = message['senderId'];
        const body = message['body'];
        const chatRoomId = context.params.chatRoomId;
        return admin.firestore().doc('ChatRooms/'+ chatRoomId).get().then(chatRoomDoc => {
            const users = chatRoomDoc.get('users');
            users.forEach((user, index) => {
                if (user !== senderId) {
                    const recipientId = user;
                    return admin.firestore().doc('Users/' + senderId).get().then(senderDoc => {
                        const senderName = senderDoc.get('name');
                        const senderPhotoUrl = senderDoc.get('url_photo');
                        return admin.firestore().doc('Users/' + recipientId).get().then(userDoc => {
                            const token = userDoc.get('token')
                            const notificationBody = body;
                            const payload = {
                                notification: {
                                    title: senderName + " sent you a message.",
                                    body: notificationBody,
                                    clickAction: "ChatRoomActivity"
                                },
                                data: {
                                    user_email: senderId,
                                    user_name: senderName,
                                    user_url_photo: senderPhotoUrl
                                }
                            }
                            return admin.messaging().sendToDevice(token, payload).then( response => {
                            })
                        })
                    })
                }
                    
            })
        })
    })