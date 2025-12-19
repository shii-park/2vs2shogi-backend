package com.github.com.shii_park.shogi2vs2.dto.auth;
import lombok.Data;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;

@Data
public class LoginRequest {
    @NotBlank(message = "ユーザー名は必須です")
    @Size(min = 1,max = 20,message = "ユーザー名は１文字以上２０文字以内で入力してください")
    private String username;   //ユーザー名を受け取る 
}
