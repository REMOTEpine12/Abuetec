package com.example.aplicacion;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiService {
    @FormUrlEncoded
    @POST("register_user.php")
    Call<ApiResponse> registerUser(
            @Field("Nombre") String nombre,
            @Field("Correo") String correo,
            @Field("Contraseña") String password,
            @Field("Telefono") String telefono,
            @Field("Genero") String genero,
            @Field("Edad") int edad
    );
}
