const functions = require('firebase-functions/v1');
const admin = require("firebase-admin");

admin.initializeApp();

exports.detectMeetingJoinWithFCM = functions.firestore
  .document("/DETAILMEETING/{documentId}")
  .onUpdate((change, context) => {
    // documentId는 알아서 detect한다.
    const documentId = context.params.documentId;

    // 문서가 삭제된 경우는 처리하지 않음
    if (!change.after.exists) {
      functions.logger.log("Document deleted", documentId);
      return null;
    }

    const newValue = change.after.data();
    const previousValue = change.before.data();

    // 문서가 업데이트된 경우
    if (newValue.participants !== previousValue?.participants) {
      const participants = newValue.participants;
      const meetingContent = newValue.meeting;
      functions.logger.log("Detected write", documentId, participants);
      // topic으로 fcm 전송
      const topic = documentId; // 모임을 토픽으로
      const message = {
        notification: {
          title: `${meetingContent}`,
          body: `이제 모임에 참가자가 ${participants.length}명이 되었습니다`
        },
        topic: topic
      };

      return admin.messaging().send(message)
      .then(response => {
        console.log('Successfully sent message:', response);
        return null;
      })
      .catch(error => {
        console.error('Error sending message:', error);
        return null;
      });
    }

    // 아무 변경도 없을 경우
    return null;
  });

exports.detectMeetingDeleteWithFCM = functions.firestore
  .document("/DETAILMEETING/{documentId}")
  .onDelete((snap, context) => {
    const documentId = context.params.documentId;
    const deletedValue = snap.data();
    
    if (deletedValue?.participants.length > 1) {
      const participants = deletedValue.participants;
      const meetingContent = deletedValue.meeting;
      functions.logger.log("Detected delete", documentId, participants);

      const topic = documentId; // 모임을 토픽으로
      const message = {
        notification: {
          title: `${meetingContent}`,
          body: `모임이 취소되었습니다`
        },
        topic: topic
      };

      return admin.messaging().send(message)
      .then(response => {
        console.log('Successfully sent message:', response);
        return null;
      })
      .catch(error => {
        console.error('Error sending message:', error);
        return null;
      });
    }

    // 아무 변경도 없을 경우
    return null;
  });
