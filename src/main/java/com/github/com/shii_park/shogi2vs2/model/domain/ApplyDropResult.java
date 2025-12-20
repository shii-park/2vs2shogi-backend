package com.github.com.shii_park.shogi2vs2.model.domain;

/**
 * ApplyDropResultは手駒から駒を配置する操作が成功したかを表すレコードクラスです
 * 
 * @param success {@code true}:成功
 * @author Suiren91
 */
public record ApplyDropResult(boolean success) {
}
