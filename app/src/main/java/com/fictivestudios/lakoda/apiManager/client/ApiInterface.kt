package com.fictivestudios.docsvisor.apiManager.client

import com.fictivestudios.lakoda.apiManager.response.*
import com.fictivestudios.ravebae.utils.Constants.Companion.ACCEPT_FOLLOW_URL
import com.fictivestudios.ravebae.utils.Constants.Companion.BERBIX_CLIENT_TOKEN_URL
import com.fictivestudios.ravebae.utils.Constants.Companion.BLOCK_LIST_URL
import com.fictivestudios.ravebae.utils.Constants.Companion.BLOCK_USER_URL
import com.fictivestudios.ravebae.utils.Constants.Companion.BUNDLE_LIST_URL
import com.fictivestudios.ravebae.utils.Constants.Companion.BUY_BUNDLE_URL
import com.fictivestudios.ravebae.utils.Constants.Companion.CHAT_ATTACHMENT
import com.fictivestudios.ravebae.utils.Constants.Companion.CREATE_COMMENT_URL
import com.fictivestudios.ravebae.utils.Constants.Companion.CREATE_FOLLOW_REQ_URL
import com.fictivestudios.ravebae.utils.Constants.Companion.CREATE_POST_URL
import com.fictivestudios.ravebae.utils.Constants.Companion.CREATE_STORY_URL
import com.fictivestudios.ravebae.utils.Constants.Companion.DELETE_ACCOUNT_URL
import com.fictivestudios.ravebae.utils.Constants.Companion.FORGET_PASSWORD_URL
import com.fictivestudios.ravebae.utils.Constants.Companion.GET_CHAT_LIST_URL
import com.fictivestudios.ravebae.utils.Constants.Companion.GET_COMMENT_URL
import com.fictivestudios.ravebae.utils.Constants.Companion.GET_CONTENT_URL
import com.fictivestudios.ravebae.utils.Constants.Companion.GET_FOLLOW_REQUEST_URL
import com.fictivestudios.ravebae.utils.Constants.Companion.GET_MY_FOLLOWERS_URL
import com.fictivestudios.ravebae.utils.Constants.Companion.GET_MY_FOLLOWINGS_URL
import com.fictivestudios.ravebae.utils.Constants.Companion.GET_NOTFICATIONS_URL
import com.fictivestudios.ravebae.utils.Constants.Companion.GET_STORY_URL
import com.fictivestudios.ravebae.utils.Constants.Companion.ID_VERIFICATION_URL
import com.fictivestudios.ravebae.utils.Constants.Companion.LIKE_UNLIKE_POST_URL
import com.fictivestudios.ravebae.utils.Constants.Companion.LOGIN_URL
import com.fictivestudios.ravebae.utils.Constants.Companion.LOGOUT_URL
import com.fictivestudios.ravebae.utils.Constants.Companion.NOTIFICATION_TOGGLE_URL
import com.fictivestudios.ravebae.utils.Constants.Companion.OTHER_USER_PROFILE_URL
import com.fictivestudios.ravebae.utils.Constants.Companion.PROFILE_URL
import com.fictivestudios.ravebae.utils.Constants.Companion.REMOVE_FOLLOW_URL
import com.fictivestudios.ravebae.utils.Constants.Companion.RESEND_OTP_URL
import com.fictivestudios.ravebae.utils.Constants.Companion.RESET_PASSWORD_URL
import com.fictivestudios.ravebae.utils.Constants.Companion.SHARE_POST_URL
import com.fictivestudios.ravebae.utils.Constants.Companion.SIGNUP_URL
import com.fictivestudios.ravebae.utils.Constants.Companion.SOCIAL_LOGIN_URL
import com.fictivestudios.ravebae.utils.Constants.Companion.UNFOLLOW_URL
import com.fictivestudios.ravebae.utils.Constants.Companion.UPDATE_PROFILE_URL
import com.fictivestudios.ravebae.utils.Constants.Companion.VERIFY_OTP_URL
import com.fictivestudios.ravebae.utils.Constants.Companion.VIEW_POST_URL
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiInterface {

    /*********************************** REGISTRATION ******************************************/


    @POST(LOGIN_URL)
    fun login(@Body info: RequestBody): retrofit2.Call<LoginResponse>

    @POST(SOCIAL_LOGIN_URL)
    fun socialLogin(@Body info: RequestBody): retrofit2.Call<SocialLoginResponse>

    @Multipart
    @POST(SIGNUP_URL)
    fun signup(@PartMap hashMap: HashMap<String, RequestBody>, @Part image: MultipartBody.Part?): retrofit2.Call<SignupResponse>

    @POST(VERIFY_OTP_URL)
    fun verifyOTP(@Body info: RequestBody): retrofit2.Call<VerifyOtpResponse>

    @POST(RESEND_OTP_URL)
    fun resendOTP(@Body info: RequestBody): retrofit2.Call<CommonResponse>

    @POST(FORGET_PASSWORD_URL)
    fun forgetPassword(@Body info: RequestBody): retrofit2.Call<ForgetPasswordResponse>

    @POST(RESET_PASSWORD_URL)
    fun resetPassword(@Body info: RequestBody): retrofit2.Call<CommonResponse>

    @Multipart
    @POST(UPDATE_PROFILE_URL)
    fun updateProfile(@PartMap hashMap: HashMap<String, RequestBody>, @Part image: MultipartBody.Part?): retrofit2.Call<UpdateProfileResponse>

    @GET(GET_CONTENT_URL)
    fun getContent(@Query("type") type:String): retrofit2.Call<ContentResponse>

    @GET(LOGOUT_URL)
    fun logout(): retrofit2.Call<CommonResponse>

    @DELETE(DELETE_ACCOUNT_URL)
    fun deleteAccount(): retrofit2.Call<CommonResponse>

    @POST(ID_VERIFICATION_URL)
    fun idVerification(@Query("id") type:String): retrofit2.Call<IdVerifyResponse>

    @GET(BERBIX_CLIENT_TOKEN_URL)
    fun getBerbixToken(): retrofit2.Call<BerbixTokenResponse>

    /***********************************PROFILE & POSTS ******************************************/


    @GET(PROFILE_URL)
    fun getProfile(): retrofit2.Call<GetMyProfileResponse>

    @POST(OTHER_USER_PROFILE_URL)
    fun getOtherUserProfile(@Query("other_id") other_id:Int): retrofit2.Call<GetMyProfileResponse>

    @POST(BLOCK_USER_URL)
    fun blockUser(@Query("blocked_user_id") blocked_user_id:Int,@Query("type") type:String): retrofit2.Call<CommonResponse>

    @GET(BLOCK_LIST_URL)
    fun getBlockList(): retrofit2.Call<BlockedListResponse>

    @Multipart
    @POST(CREATE_POST_URL)
    fun createPost(@PartMap hashMap: HashMap<String, RequestBody>, @Part image: MultipartBody.Part?): retrofit2.Call<CreatePostResponse>

    @GET(SHARE_POST_URL)
    fun sharePost(@Path("id")id:Int): retrofit2.Call<CommentResponse>

    @GET(VIEW_POST_URL)
    fun getAllPost(@Query("type") type:String): retrofit2.Call<HomePostResponse>


    @Multipart
    @POST(CREATE_COMMENT_URL)
    fun createComment(@PartMap hashMap: HashMap<String, RequestBody>, @Part image: MultipartBody.Part?): retrofit2.Call<CommonResponse>

    @POST(GET_COMMENT_URL)
    fun getComments(@Query("post_id") post_id:Int,@Query("type") type:String): retrofit2.Call<GetCommentsResponse>


    @POST(LIKE_UNLIKE_POST_URL)
    fun likePost(@Query("post_id") post_id:Int,@Query("type") type:String): retrofit2.Call<CommonResponse>

    @POST(GET_COMMENT_URL)
    fun getLikes(@Query("post_id") post_id:Int,@Query("type") type:String): retrofit2.Call<GetLikesResponse>



    /***********************************FOLLOW, FOLLOWING ******************************************/


    @POST(CREATE_FOLLOW_REQ_URL)
    fun followRequest(@Query("user_id")user_id:Int): retrofit2.Call<CommonResponse>

    @DELETE(UNFOLLOW_URL)
    fun unFollowRequest(@Path("id")id:Int): retrofit2.Call<CommonResponse>

    @DELETE(REMOVE_FOLLOW_URL)
    fun removeFollowRequest(@Path("id")id:Int): retrofit2.Call<CommonResponse>

    @GET(GET_MY_FOLLOWERS_URL)
    fun getMyFollowers(@Query("user_id")user_id:Int): retrofit2.Call<GetFollowingResponse>

    @GET(GET_MY_FOLLOWINGS_URL)
    fun getMyFollowings(@Query("user_id")user_id:Int): retrofit2.Call<GetFollowingResponse>

    @GET(GET_FOLLOW_REQUEST_URL)
    fun getFollowRequest(): retrofit2.Call<GetFollowRequest>

    @POST(ACCEPT_FOLLOW_URL)
    fun acceptFollowRequest(@Path("id")id:Int,@Body info: RequestBody): retrofit2.Call<CommonResponse>

    /*********************************** STORY ******************************************/
    @Multipart
    @POST(CREATE_STORY_URL)
    fun createStory(@Part image: MultipartBody.Part?,@Query("duration")duration:Int): retrofit2.Call<CommonResponse>

    @GET(GET_STORY_URL)
    fun getStories(): retrofit2.Call<GetStoryResponse>


    /*********************************** NOTIFICATIONS ******************************************/
    @GET(GET_NOTFICATIONS_URL)
    fun getNotifications(): retrofit2.Call<GetNotificationsResponse>

    @GET(NOTIFICATION_TOGGLE_URL)
    fun notificationToggle(): retrofit2.Call<NotificationToggleResponse>

    /*********************************** CHAT ******************************************/

    @GET(GET_CHAT_LIST_URL)
    fun getChatList(): retrofit2.Call<GetChatListResponse>

    @Multipart
    @POST(CHAT_ATTACHMENT)
    fun chatAttachment(@Query("receiver_id")receiver_id:Int,@Query("sender_id")sender_id:Int,@Query("type")type:String,@Part message : MultipartBody.Part?): retrofit2.Call<ChatAttachmentResponse>

    /*********************************** BUNDLES ******************************************/

    @GET(BUNDLE_LIST_URL)
    fun bundleList(): retrofit2.Call<BundleResponse>


    @POST(BUY_BUNDLE_URL)
    fun buyBundle(@Query("bundle_type") bundle_id:String, @Query("receipt") receipt:String,
                  @Query("source") source:String, @Query("home_limit") home_limit: Int?, @Query("post_limit") post_limit: Int?
    ): retrofit2.Call<BuyBundleResponse>
}