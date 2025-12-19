package com.github.com.shii_park.shogi2vs2.dto.auth;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "ユーザー名は必須です")
    @Size(min = 1,max = 20,message = "ユーザー名は1文字以上20文字以内で入力してください")
    private String username;   //ユーザー名を受け取る 
}
