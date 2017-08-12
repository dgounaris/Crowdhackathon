package dgounaris.dev.sch.APIHandler;

import java.util.List;

import dgounaris.dev.sch.Bins.Bin;
import dgounaris.dev.sch.People.Person;
import dgounaris.dev.sch.People.Service;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.POST;


/**
 * Created by DimitrisLPC on 3/8/2017.
 */

public interface ApiInterface {

    @FormUrlEncoded
    @POST("/person/register")
    Call<Person> registerPerson(
            @Field("username") String username,
            @Field("password") String password,
            @Field("name") String name,
            @Field("surname") String surname
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

    //get available services
    @GET("/services/available") //todo CHANGE THIS TO FILTER BY TOWN
    Call<List<Service>> availableServices();

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
