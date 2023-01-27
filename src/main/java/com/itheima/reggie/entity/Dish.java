package com.itheima.reggie.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 菜品
 */
@Data
public class Dish implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;


    //菜品名称
    private String name;


    //菜品分类id
    private Long categoryId;


    /**
     * 在《Effective Java》这本书中也提到这个原则，float和double只能用来做科学计算或者是工程计算，
     * 在商业计算中我们要用java.math.BigDecimal。
     * MySQL BigDecimal在进行入库时, 数据库选择decimal类型,
     * 长度可以自定义, 如18; 小数点我们项目中用的是2, 保留2位小数. 此外还要注意的就是默认值,
     * 一定写成0.00, 不要用默认的NULL, 否则在进行加减排序等操作时, 会带来转换的麻烦!
     */
    //菜品价格
    private BigDecimal price;


    //商品码
    private String code;


    //图片
    private String image;


    //描述信息
    private String description;


    //0 停售 1 起售
    private Integer status;


    //顺序
    private Integer sort;


    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;


    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;


    @TableField(fill = FieldFill.INSERT)
    private Long createUser;


    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;

}
