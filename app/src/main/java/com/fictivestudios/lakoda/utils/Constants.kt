package com.fictivestudios.ravebae.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap

import android.media.MediaMetadataRetriever
import android.media.MediaMetadataRetriever.METADATA_KEY_DURATION
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Parcelable
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.DisplayMetrics
import android.util.Log
import com.fictivestudios.lakoda.model.User
import com.fictivestudios.lakoda.utils.PreferenceUtils
import com.fictivestudios.lakoda.utils.getMimeType
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.regex.Pattern

class Constants {
    companion object {



        /*********************************** API URL ******************************************/



        val BASE_URL: String = "https://server.appsstaging.com/3373/lakoda/public/api/"
        val IMAGE_BASE_URL: String = "https://server.appsstaging.com/3373/lakoda/public/storage/"
        val THUMB_BASE_URL: String = "https://server.appsstaging.com/3373/lakoda/public/"

        val SOCKET_URL: String = "https://server.appsstaging.com:3059/"


        const val SOCIAL_LOGIN_URL = "social_login"
        const val LOGIN_URL = "login"
        const val SIGNUP_URL = "sign-up"
        const val VERIFY_OTP_URL = "verification"
        const val RESEND_OTP_URL = "resend-code"
        const val FORGET_PASSWORD_URL = "forgot-password"
        const val RESET_PASSWORD_URL = "reset-password"
        const val UPDATE_PROFILE_URL = "update-profile"
        const val GET_CONTENT_URL = "content"
        const val LOGOUT_URL = "logout"
        const val ID_VERIFICATION_URL = "ID-verification"
        const val BERBIX_CLIENT_TOKEN_URL = "client-token"

        const val PROFILE_URL = "complete-profile"
        const val OTHER_USER_PROFILE_URL = "user-profile"
        const val BLOCK_USER_URL = "user/block"
        const val BLOCK_LIST_URL = "user/block-list"

        const val CREATE_POST_URL = "post/create"
        const val SHARE_POST_URL = "post/share/{id}"
        const val VIEW_POST_URL = "post/list"
        const val CREATE_COMMENT_URL = "post/comment"
        const val GET_COMMENT_URL = "post/comments"
        const val LIKE_UNLIKE_POST_URL = "post/like"
        const val GET_LIKES_URL = "post/likes"

        const val CREATE_FOLLOW_REQ_URL = "follow/create"
        const val UNFOLLOW_URL = "follow/following/{id}"
        const val REMOVE_FOLLOW_URL = "follow/follower/{id}"
        const val GET_MY_FOLLOWERS_URL = "follow/followers"
        const val GET_MY_FOLLOWINGS_URL = "follow/followings"
        const val GET_FOLLOW_REQUEST_URL = "follow/requests"
        const val ACCEPT_FOLLOW_URL = "notification/follow-request/{id}"

        const val CREATE_STORY_URL = "stories/create"
        const val GET_STORY_URL = "stories/list"
        const val GET_NOTFICATIONS_URL = "notification/list"
        const val GET_CHAT_LIST_URL = "chats/list"
        const val CHAT_ATTACHMENT = "chats/attachments"

        const val NOTIFICATION_TOGGLE_URL = "notification/toggle"

        const val BUNDLE_LIST_URL = "bundle/list"

        const val BUY_BUNDLE_URL = "subscription/buy-bundle"
        /*



               const val CHANGE_PASSWORD_URL = "docviser/api/auth/password/change"
               const val RESET_PASSWORD_URL = "docviser/api/auth/password/reset"*/


        /*********************************** API URL ******************************************/


        const val IS_USER = "is_user"
        const val NOTIFICATION_TOGGLE = "notificationToggle"
        const val IS_FIRST_TIME ="first_time"
        var isAccountLogin = false
        var loginType:String? =null
        const val EMAIL = "email"
        const val BERBIX_TOKEN = "berbixToken"
        const val IS_ID_CARD_VERIFIED = "isIdVerified"
        const val STATUS_VERIFIED ="verified"
        const val STATUS_NOT_VERIFIED ="not-verified"
        const val STATUS_REJECTED ="rejected"
        const val PHONE ="phone"

        const val STORY_DURATION="storyDuration"
        const val USER_ID="userId"
        const val IS_REMOVE="isRemove"
        const val POST_ID="postId"
        const val USER_OBJECT = "userObj"
        const val VIEW_TYPE_MESSAGE_SENT = 12
        const val VIEW_TYPE_MESSAGE_RECEIVED = 15

        const val VIEW_TYPE_POST = 1
        const val VIEW_TYPE_SHARED_POST = 0
        const val POST_TYPE = "postType"

        var FCM="fcmToken"
        var CURRENT_USER_ID ="userId"

        var videPagePostion = 0
        var VERIFY_TYPE_ACCOUNT = "verification"
        var VERIFY_TYPE_PASSWORD = "forgot"
        var ACCESS_TOKEN = "accesToken"
        const val COMMENTS ="comments"
        const val SHARER_COMMENTS ="sharerComments"
        const val LIKES ="likes"
        const val SHARER_LIKES ="sharerLikes"
        const val PROFILE ="profile"
        const val SHARER_PROFILE ="sharerProfile"
        const val TYPE_POST ="post"
        const val TYPE_SHARE ="share"
        const val FOLLOW ="follow"
        const val UNFOLLOW ="unfollow"
        const val REMOVE_FOLLOW ="removeFollow"
        const val VIEW_STORY ="viewStory"
        const val CREATE_STORY ="createStory"
        const val IMAGE ="image"
        const val USER_NAME ="userName"
        const val USER_IMAGE ="Userimage"
        const val ACCEPT ="accept"
        const val REJECT ="reject"
        const val MESSAGE ="message"
        const val LIKED ="liked"
        const val UNLIKED ="unliked"

        const val TYPE_TEXT = "text"
        const val TYPE_IMAGE = "image"
        const val TYPE_LOCATION = "location"
        const val TYPE_DOCUMENT = "document"
        const val TYPE_VIDEO = "video"
        const val MESSAGE_TYPE ="messageType"

        const val NOTIFICATION_TYPE = "type"

        const val BLOCK ="block"
        const val UNBLOCK ="unblock"

        const val STATUS_FOLLOWED ="followed"
        const val STATUS_UNFOLLOWED ="unfollowed"
        const val STATUS_REQUEST_SENT ="sent"
        const val RECEIVER_USER_ID = "receiverUserId"
         var state: Parcelable? = null

        var IS_PUSH = false

        fun isValidEmail(str: String): Boolean{
            return android.util.Patterns.EMAIL_ADDRESS.matcher(str).matches()
        }

        fun CharSequence.isValidPassword(): Boolean {
            val passwordPattern = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$"
            val pattern = Pattern.compile(passwordPattern)
            val matcher = pattern.matcher(this)
            return matcher.matches()
        }

        fun getUser():User{
            var gson =  Gson()
            var json:String= PreferenceUtils.getString(USER_OBJECT)
            return  gson.fromJson(json,User::class.java)
        }

        fun String.getTime(input: String, output: String): String {
            val inputFormat = SimpleDateFormat(input)
            val outputFormat = SimpleDateFormat(output)
            val date = inputFormat.parse(this)
            return outputFormat.format(date ?: "")
        }

        fun File.getMediaDuration(context: Context): Long {
            if (!exists()) return 0
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(context, Uri.parse(absolutePath))
            val duration = retriever.extractMetadata(METADATA_KEY_DURATION)
            retriever.release()

            return duration?.toLongOrNull() ?: 0
        }


        fun getMediaFilePathFor(
            uri: Uri,
            context: Context
        ): String {
            val returnCursor =
                context.contentResolver.query(uri, null, null, null, null)
            val nameIndex = returnCursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE)
            returnCursor.moveToFirst()
            val name = returnCursor.getString(nameIndex)
            val size = returnCursor.getLong(sizeIndex).toString()
            val file = File(context.filesDir, name)
            try {
                val inputStream =
                    context.contentResolver.openInputStream(uri)
                val outputStream = FileOutputStream(file)
                var read = 0
                val maxBufferSize = 1 * 1024 * 1024
                val bytesAvailable = inputStream!!.available()
                //int bufferSize = 1024;
                val bufferSize = bytesAvailable.coerceAtMost(maxBufferSize)
                val buffers = ByteArray(bufferSize)
                while (inputStream.read(buffers).also { read = it } != -1) {
                    outputStream.write(buffers, 0, read)
                }
                Log.e("File Size %d", "" + file.length())
                inputStream.close()
                outputStream.close()
                Log.e("File Size %s", file.path)
                Log.e("File Size %d", "" + file.length())
            } catch (e: java.lang.Exception) {
                Log.e("File Size %s", e.message!!)
            }
            return file.path
        }

        fun prepareFilePart(partName: String, file: File): MultipartBody.Part {

            //  val requestFile = RequestBody.create("video/mp4".toMediaTypeOrNull(), file)
            val requestFile = file.asRequestBody(file.absolutePath.getMimeType().toMediaTypeOrNull())
            return MultipartBody.Part.createFormData(partName, file.name, requestFile)
        }

        fun createVideoThumbNail(path: String?): Bitmap? {
            return ThumbnailUtils. createVideoThumbnail(path!!, MediaStore.Video.Thumbnails.FULL_SCREEN_KIND)
        }

    }



}