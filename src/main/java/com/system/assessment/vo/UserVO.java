package com.system.assessment.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.system.assessment.constants.Role;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Data
public class  UserVO {
    private Integer id;

    private Integer role;

    private String username;

    private String workNum;

    private String name;

    private String password;

    private String residence; //常住地

    private String department;

    private String email;

    private String phoneNumber;

    private Integer supervisor1; //主管1 id

    private Integer supervisor2; //主管2 id

    private Integer supervisor3; //主管3 id

    private Integer supervisor4; //主管4 id

    private Integer hr;

    private String supervisorName1; //主管1姓名

    private String supervisorName2; //主管2姓名

    private String supervisorName3; //主管3姓名

    private String supervisorName4; //主管4姓名

    private String hrName;

    private Double weight;

    private String lxyz; //LXYZ类型

    private String businessType; //业务类型

    @JsonFormat(pattern="yyyy-MM-dd",timezone="GMT+8")
    private LocalDate hireDate; //入职时间

    @JsonFormat(pattern="yyyy-MM-dd",timezone="GMT+8")
    private LocalDate resignationDate; //离职时间

    @JsonIgnore
    private Boolean enabled;//账户是否激活

    @JsonIgnore
    private Boolean accountNonExpired;//账户是否过期

    @JsonIgnore
    private Boolean accountNonLocked; //账户是否被锁定

    @JsonIgnore
    private Boolean credentialsNonExpired;//密码是否过期

    @JsonIgnore
    private Boolean isDelete;

    public void  preHandle(){
        if(supervisor1 == null){
            supervisor1 = 0;
        }
        if(supervisor2 == null){
            supervisor2 = 0;
        }
        if(supervisor3 == null){
            supervisor3 = 0;
        }
        if(supervisor4 == null){
            supervisor4 = 0;
        }
    };



}
