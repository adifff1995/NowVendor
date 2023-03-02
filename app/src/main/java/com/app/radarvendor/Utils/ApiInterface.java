package com.app.radarvendor.Utils;


import com.app.radarvendor.Module.About;
import com.app.radarvendor.Module.ActiveOrders;
import com.app.radarvendor.Module.Category;
import com.app.radarvendor.Module.City;
import com.app.radarvendor.Module.DeliveryType;
import com.app.radarvendor.Module.NotificationItem;
import com.app.radarvendor.Module.Offers;
import com.app.radarvendor.Module.OrderFromUser;
import com.app.radarvendor.Module.OrderResponse;
import com.app.radarvendor.Module.Product;
import com.app.radarvendor.Module.ProductImages;
import com.app.radarvendor.Module.Rating;
import com.app.radarvendor.Module.Reservations;
import com.app.radarvendor.Module.ResultAPIModel;
import com.app.radarvendor.Module.Service;
import com.app.radarvendor.Module.ServiceType;
import com.app.radarvendor.Module.Tag;
import com.app.radarvendor.Module.Vendor;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiInterface {

    @POST("store/login")
    @FormUrlEncoded
    Call<ResultAPIModel<Vendor>> login(@FieldMap Map<String, Object> params);

    @POST("delivery/login")
    @FormUrlEncoded
    Call<ResultAPIModel<Vendor>> loginDelivery(@FieldMap Map<String, Object> params);

    @POST("store/verify")
    @FormUrlEncoded
    Call<ResultAPIModel<Vendor>> verify(@FieldMap Map<String, Object> params);

    @POST("store/resendCode")
    @FormUrlEncoded
    Call<ResultAPIModel> resendCode(@FieldMap Map<String, Object> params);

    @POST("delivery/resendCode")
    @FormUrlEncoded
    Call<ResultAPIModel> resendCodeDelivery(@FieldMap Map<String, Object> params);

    @POST("store/passwordReset")
    @FormUrlEncoded
    Call<ResultAPIModel> passwordReset(@FieldMap Map<String, Object> params);

    @POST("store/categories/store")
    @FormUrlEncoded
    Call<ResultAPIModel> addCategory(@HeaderMap Map<String, Object> headers, @FieldMap Map<String, Object> params);


    @Multipart
    @POST("store/register")
    Call<ResultAPIModel<Vendor>> register(@Part("name") RequestBody name,
                                          @Part("email") RequestBody email,
                                          @Part("phone") RequestBody phone,
                                          @Part("password") RequestBody password,
                                          @Part("password_confirmation") RequestBody password_confirmation,
                                          @Part("latitude") RequestBody latitude,
                                          @Part("longitude") RequestBody longitude,
                                          @Part("fcm") RequestBody fcm,
                                          @Part("type") RequestBody type,
                                          @Part("service_id") RequestBody service_id,
                                          @Part("service_description") RequestBody service_description,
                                          @Part("work_days") RequestBody work_days,
                                          @Part("min_order") RequestBody min_order,
                                          @Part("radius_work") RequestBody radius_work,
                                          @Part("tags") RequestBody tags,
                                          @Part MultipartBody.Part icon_url);

    @Multipart
    @POST("store/checkRegister")
    Call<ResultAPIModel> checkRegister(@Part("name") RequestBody name,
                                       @Part("email") RequestBody email,
                                       @Part("phone") RequestBody phone,
                                       @Part("password") RequestBody password,
                                       @Part("password_confirmation") RequestBody password_confirmation,
                                       @Part MultipartBody.Part icon_url);

    @POST("getServiceType")
    @FormUrlEncoded
    Call<ResultAPIModel<List<ServiceType>>> getServiceType(@FieldMap Map<String, Object> params);

    @GET("store/categories/getCategoriesByUser")
    Call<ResultAPIModel<List<Category>>> getCategoriesByUser(@HeaderMap Map<String, Object> headers);

    @GET("store/products")
    Call<ResultAPIModel<List<Product>>> getProducts(@HeaderMap Map<String, Object> headers);

    @GET("store/services")
    Call<ResultAPIModel<List<Service>>> getServices(@HeaderMap Map<String, Object> headers);

    @POST("store/products/getProductsByStatus")
    @FormUrlEncoded
    Call<ResultAPIModel<List<Product>>> getArchivedProducts(@HeaderMap Map<String, Object> headers, @FieldMap Map<String, Object> params);

    @POST("store/products/getProductsByCategory")
    @FormUrlEncoded
    Call<ResultAPIModel<List<Product>>> getProductsByCategory(@HeaderMap Map<String, Object> headers, @FieldMap Map<String, Object> params);

    @POST("store/products/getProductsByCategory")
    @FormUrlEncoded
    Call<ResultAPIModel<List<Product>>> getProductByCategory(@HeaderMap Map<String, Object> headers, @FieldMap Map<String, Object> params);


    @Multipart
    @POST("store/products")
    Call<ResultAPIModel> addProduct(@HeaderMap Map<String, Object> headers,
                                    @Part("name") RequestBody name,
                                    @Part("category_id") RequestBody category_id,
                                    @Part("price") RequestBody price,
                                    @Part("offer_price") RequestBody offer_price,
                                    @Part("quantity") RequestBody quantity,
                                    @Part("description") RequestBody description,
                                    @Part("has_details") RequestBody has_details,
                                    @Part("additions[]") RequestBody additions,
                                    @Part("unique_additions[]") RequestBody uniqueAdditions,
                                    @Part MultipartBody.Part[] Image);

//    @Multipart
//    @POST("store/products/update")
//    Call<ResultAPIModel> updateProduct(@HeaderMap Map<String, Object> headers,
//                                       @Part("product_id") RequestBody product_id,
//                                       @Part("name") RequestBody name,
//                                       @Part("category_id") RequestBody category_id,
//                                       @Part("price") RequestBody price,
//                                       @Part("offer_price") RequestBody offer_price,
//                                       @Part("quantity") RequestBody quantity,
//                                       @Part("description") RequestBody description,
//                                       @Part("additions[]") RequestBody additions,
//                                       @Part MultipartBody.Part[] Image);

    @POST("store/products/update")
    @FormUrlEncoded
    Call<ResultAPIModel> updateProduct(@HeaderMap Map<String, Object> headers, @FieldMap Map<String, Object> params);

    @POST("store/products/deleteImageProductById")
    @FormUrlEncoded
    Call<ResultAPIModel> deleteImageProductById(@HeaderMap Map<String, Object> headers, @FieldMap Map<String, Object> params);

    @POST("store/products/getProductsById")
    @FormUrlEncoded
    Call<ResultAPIModel<Product>> getProductsById(@HeaderMap Map<String, Object> headers, @FieldMap Map<String, Object> params);

    @POST("store/services/getServiceById")
    @FormUrlEncoded
    Call<ResultAPIModel<Service>> getServiceById(@HeaderMap Map<String, Object> headers, @FieldMap Map<String, Object> params);

    @POST("store/products/updateStatus")
    @FormUrlEncoded
    Call<ResultAPIModel> updateStatus(@HeaderMap Map<String, Object> headers, @FieldMap Map<String, Object> params);

    @POST("store/profile/changePassword")
    @FormUrlEncoded
    Call<ResultAPIModel> changePassword(@HeaderMap Map<String, Object> headers, @FieldMap Map<String, Object> params);

    @POST("delivery/profile/changePassword")
    @FormUrlEncoded
    Call<ResultAPIModel> changePasswordD(@HeaderMap Map<String, Object> headers, @FieldMap Map<String, Object> params);

    @Multipart
    @POST("store/services")
    Call<ResultAPIModel> addService(@HeaderMap Map<String, Object> headers,
                                    @Part("name") RequestBody name,
                                    @Part("service_time") RequestBody service_time,
                                    @Part("price") RequestBody price,
                                    @Part("offer_price") RequestBody offer_price,
                                    @Part("description") RequestBody description,
                                    @Part("additions[]") RequestBody additions,
                                    @Part MultipartBody.Part Image);

    @Multipart
    @POST("store/services/update")
    Call<ResultAPIModel> updateService(@HeaderMap Map<String, Object> headers,
                                       @Part("service_id") RequestBody service_id,
                                       @Part("name") RequestBody name,
                                       @Part("service_time") RequestBody service_time,
                                       @Part("price") RequestBody price,
                                       @Part("offer_price") RequestBody offer_price,
                                       @Part("description") RequestBody description,
                                       @Part("additions[]") RequestBody additions,
                                       @Part MultipartBody.Part Image);

    @GET("store/profile")
    Call<ResultAPIModel<Vendor>> getProfile(@HeaderMap Map<String, Object> headers);

    @Multipart
    @POST("store/profile/updateProfile")
    Call<ResultAPIModel<Vendor>> updateProfile(
            @HeaderMap Map<String, Object> headers,
            @Part("name") RequestBody name,
            @Part("email") RequestBody email,
            @Part("phone") RequestBody phone,
            @Part("latitude") RequestBody latitude,
            @Part("longitude") RequestBody longitude,
            @Part("fcm") RequestBody fcm,
            @Part("service_id") RequestBody service_id,
            @Part("service_description") RequestBody service_description,
            @Part("work_days") RequestBody work_days,
            @Part("radius_work") RequestBody radius_work,
            @Part("tags") RequestBody tags,
//            @Part("work_time_from") RequestBody work_time_from,
//            @Part("work_time_to") RequestBody work_time_to,
            @Part MultipartBody.Part icon_url);

    @GET("about")
    Call<ResultAPIModel<About>> about();

    @POST("store/categories/searchCategoryByname")
    @FormUrlEncoded
    Call<ResultAPIModel<List<Category>>> searchCategory(@HeaderMap Map<String, Object> headers, @FieldMap Map<String, Object> params);

    @POST("store/products/searchProductsByname")
    @FormUrlEncoded
    Call<ResultAPIModel<List<Product>>> searchProducts(@HeaderMap Map<String, Object> headers, @FieldMap Map<String, Object> params);

    @POST("store/services/searchServicesByname")
    @FormUrlEncoded
    Call<ResultAPIModel<List<Service>>> searchServices(@HeaderMap Map<String, Object> headers, @FieldMap Map<String, Object> params);

    @POST("contact_us")
    @FormUrlEncoded
    Call<ResultAPIModel> contactUs(@HeaderMap Map<String, Object> headers, @FieldMap Map<String, Object> params);

    @Multipart
    @POST("store/products/uploadImageProduct")
    Call<ResultAPIModel<ProductImages>> uploadImageProduct(@HeaderMap Map<String, Object> headers, @Part("product_id") RequestBody product_id,
                                                           @Part MultipartBody.Part Image);

    @POST("store/orders")
    @FormUrlEncoded
    Call<ResultAPIModel<List<OrderResponse>>> getOrders(@HeaderMap Map<String, Object> headers, @FieldMap Map<String, Object> params);

    @POST("delivery/orders")
    @FormUrlEncoded
    Call<ResultAPIModel<List<OrderResponse>>> getDeliveryOrders(@HeaderMap Map<String, Object> headers, @FieldMap Map<String, Object> params);


    @GET("store/profile/getReviews")
    Call<ResultAPIModel<Rating>> getReviews(@HeaderMap Map<String, Object> headers);


    @GET("store/profile/getNotifications")
    Call<ResultAPIModel<List<NotificationItem>>> getNotifications(@HeaderMap Map<String, Object> headers);

    @POST("store/Reservations")
    @FormUrlEncoded
    Call<ResultAPIModel<List<Reservations>>> getReservations(@HeaderMap Map<String, Object> headers, @FieldMap Map<String, Object> params);

    @POST("store/profile/readNotification")
    @FormUrlEncoded
    Call<ResultAPIModel> readNotification(@HeaderMap Map<String, Object> headers, @FieldMap Map<String, Object> params);

    @POST("store/Reservations/store")
    @FormUrlEncoded
    Call<ResultAPIModel> storeReservations(@HeaderMap Map<String, Object> headers, @FieldMap Map<String, Object> params);

    @GET("getCity")
    Call<ResultAPIModel<List<City>>> getCity();

    @GET("getTags")
    Call<ResultAPIModel<List<Tag>>> getTags();

    @Multipart
    @POST("delivery/register")
    Call<ResultAPIModel<Vendor>> registerDelivery(@Part("name") RequestBody name,
                                                  @Part("phone") RequestBody phone,
                                                  @Part("password") RequestBody password,
                                                  @Part("password_confirmation") RequestBody password_confirmation,
                                                  @Part("id_no") RequestBody id_no,
                                                  @Part("city_id") RequestBody city_id,
                                                  @Part("type") RequestBody type,
                                                  @Part("fcm") RequestBody fcm,
                                                  @Part("latitude") RequestBody latitude,
                                                  @Part("longitude") RequestBody longitude,
                                                  @Part MultipartBody.Part icon_url);


    @POST("delivery/verify")
    @FormUrlEncoded
    Call<ResultAPIModel<Vendor>> verifyDelivery(@FieldMap Map<String, Object> params);

    @GET("delivery/profile")
    Call<ResultAPIModel<Vendor>> getDeliveryProfile(@HeaderMap Map<String, Object> headers);

    @Multipart
    @POST("delivery/profile/updateProfile")
    Call<ResultAPIModel<Vendor>> updateDeliveryProfile(
            @HeaderMap Map<String, Object> headers,
            @Part("name") RequestBody name,
            @Part("phone") RequestBody phone,
            @Part("id_no") RequestBody id_no,
            @Part("city_id") RequestBody city_id,
            @Part("type") RequestBody type,
            @Part("longitude") RequestBody longitude,
            @Part("latitude") RequestBody latitude,
            @Part("fcm") RequestBody fcm,
            @Part MultipartBody.Part icon_url);

    @POST("store/orders/orderAcceptance")
    @FormUrlEncoded
    Call<ResultAPIModel<OrderResponse>> orderAcceptance(@HeaderMap Map<String, Object> headers, @FieldMap Map<String, Object> params);

    @POST("delivery/orders/orderReceived")
    @FormUrlEncoded
    Call<ResultAPIModel<OrderResponse>> orderDeliveryReceived(@HeaderMap Map<String, Object> headers, @FieldMap Map<String, Object> params);

    @POST("delivery/deliveryServices/deliveryReceived")
    @FormUrlEncoded
    Call<ResultAPIModel<OrderFromUser>> deliveryReceived(@HeaderMap Map<String, Object> headers, @FieldMap Map<String, Object> params);


    @POST("delivery/orders/orderAcceptance")
    @FormUrlEncoded
    Call<ResultAPIModel<OrderResponse>> orderDeliveryAcceptance(@HeaderMap Map<String, Object> headers, @FieldMap Map<String, Object> params);

    @POST("delivery/deliveryServices/deliveryAcceptance")
    @FormUrlEncoded
    Call<ResultAPIModel<OrderFromUser>> deliveryAcceptance(@HeaderMap Map<String, Object> headers, @FieldMap Map<String, Object> params);

    @POST("delivery/orders/orderCompleted")
    @FormUrlEncoded
    Call<ResultAPIModel<OrderResponse>> orderDeliveryCompleted(@HeaderMap Map<String, Object> headers, @FieldMap Map<String, Object> params);

    @POST("delivery/deliveryServices/deliveryCompleted")
    @FormUrlEncoded
    Call<ResultAPIModel<OrderFromUser>> deliveryCompleted(@HeaderMap Map<String, Object> headers, @FieldMap Map<String, Object> params);


    @POST("store/orders/orderReject")
    @FormUrlEncoded
    Call<ResultAPIModel<OrderResponse>> orderReject(@HeaderMap Map<String, Object> headers, @FieldMap Map<String, Object> params);

    @POST("store/orders/orderComplete")
    @FormUrlEncoded
    Call<ResultAPIModel<OrderResponse>> orderComplete(@HeaderMap Map<String, Object> headers, @FieldMap Map<String, Object> params);

    @POST("store/profile/changeStoreStatus")
    @FormUrlEncoded
    Call<ResultAPIModel<Vendor>> changeStoreStatus(@HeaderMap Map<String, Object> headers, @FieldMap Map<String, Object> params);

    @GET("store/offers")
    Call<ResultAPIModel<List<Offers>>> getOffers(@HeaderMap Map<String, Object> headers);

    @Multipart
    @POST("store/offers")
    Call<ResultAPIModel> addOffer(@HeaderMap Map<String, Object> headers,
                                  @Part("name") RequestBody name,
                                  @Part("old_price") RequestBody category_id,
                                  @Part("offer_price") RequestBody price,
                                  @Part("description") RequestBody offer_price,
                                  @Part("product_id") RequestBody additions,
                                  @Part MultipartBody.Part image);

    @GET("getTypeDelivery")
    Call<ResultAPIModel<List<DeliveryType>>> getTypeDelivery();

    @GET("delivery/profile/changeStatus")
    Call<ResultAPIModel<List<Offers>>> changeStatus(@HeaderMap Map<String, Object> headers);

    @GET("delivery/profile/orderAcvive")
    Call<ResultAPIModel<ActiveOrders>> orderActive(@HeaderMap Map<String, Object> headers);

    @POST("store/Reservations/reservationStatus")
    @FormUrlEncoded
    Call<ResultAPIModel<Reservations>> reservationStatus(@HeaderMap Map<String, Object> headers, @FieldMap Map<String, Object> params);

}
