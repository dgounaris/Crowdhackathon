package dgounaris.dev.sch.APIHandler;

import java.util.List;

import dgounaris.dev.sch.Bins.Bin;
import dgounaris.dev.sch.People.Person;
import dgounaris.dev.sch.People.Service;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.POST;


/**
 * Created by DimitrisLPC on 3/8/2017.
 */

public interface ApiInterface {

    @Multipart
    @POST("/person/register")
    Call<Person> registerPerson(
            @Part("username") String username,
            @Part("password") String password,
            @Part("name") String name,
            @Part("surname") String surname,
            @Part MultipartBody.Part image
    );

    //checks for person existance and returns person if exists otherwise empty
    @FormUrlEncoded
    @POST("/login")
    Call<Person> loginAttempt( //todo for the first test no pic is used, add it later
            @Field("username") String username,
            @Field("password") String password
    );

    //get person details if exists otherwise empty
    @GET("/person/{id}/details")
    Call<Person> personDetails(
            @Path("id") long id
    );

    //add points to person, returns person points after transaction
    @FormUrlEncoded
    @POST("/person/addpoints")
    Call<Integer> addPoints(
            @Field("id") long id,
            @Field("points") int points
    );

    @Multipart
    @POST("/person/upload")
    Call<ResponseBody> uploadImage(
            @Part("id") long id,
            @Part("image") MultipartBody.Part imgFile
    );

    //get available services
    @GET("/services/available/{cityid}")
    Call<List<Service>> availableServices(
            @Path("cityid") long cityId
    );

    //redeem a service, returns person points after transaction
    @FormUrlEncoded
    @POST("/services/redeem")
    Call<Integer> redeemService(
            @Field("personid") long personId,
            @Field("serviceid") long serviceId
    );

    //gets all the bins in database
    @GET("/bins/all")
    Call<List<Bin>> getAllBins();

    //gets top people sorted by totalPoints, number specified by "max"
    @GET("/people/toppoints/{max}")
    Call<List<Person>> getTopByTotalPoints(
            @Path("max") int max
    );

}
