package com.system.assessment.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.microsoft.schemas.vml.STTrueFalse;
import com.sun.jmx.snmp.mpm.SnmpMsgTranslator;
import com.system.assessment.constants.Role;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.*;

//自定义User

public class User implements UserDetails {

    private Integer id;

    private Integer role;

    private String username;

    private Double weight;

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

    private Integer hr;

    private String supervisorName1; //主管1姓名

    private String supervisorName2; //主管2姓名

    private String supervisorName3; //主管3姓名

    private String hrName;

    private String lxyz; //LXYZ类型

    private String businessType; //业务类型

    public Double weight1;

    public Double weight2;

    public Double weight3;

    public Integer firstAdmin;

    public Integer secondAdmin;

    public Integer superAdmin;

    public String firstAdminName;

    public String secondAdminName;

    public String superAdminName;

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

    private Boolean isFirstLogin;

    //返回权限信息
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        String roleName = Role.getDescriptionByCode(role);
        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(roleName);
        authorities.add(simpleGrantedAuthority);
        return authorities;
    }

    @Override
    public String getPassword() {
        return "{noop}" + password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getHr() {
        return hr;
    }

    public void setHr(Integer hr) {
        this.hr = hr;
    }

    public String getHrName() {
        return hrName;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public void setHrName(String hrName) {
        this.hrName = hrName;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    public void setAccountNonExpired(Boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    public void setAccountNonLocked(Boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    public void setCredentialsNonExpired(Boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled != null && enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    public String getWorkNum() {
        return workNum;
    }

    public void setWorkNum(String workNum) {
        this.workNum = workNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getResidence() {
        return residence;
    }

    public void setResidence(String residence) {
        this.residence = residence;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Integer getSupervisor1() {
        return supervisor1;
    }

    public void setSupervisor1(Integer supervisor1) {
        this.supervisor1 = supervisor1;
    }

    public Integer getSupervisor2() {
        return supervisor2;
    }

    public void setSupervisor2(Integer supervisor2) {
        this.supervisor2 = supervisor2;
    }

    public Integer getSupervisor3() {
        return supervisor3;
    }

    public void setSupervisor3(Integer supervisor3) {
        this.supervisor3 = supervisor3;
    }

    public String getSupervisorName1() {
        return supervisorName1;
    }

    public void setSupervisorName1(String supervisorName1) {
        this.supervisorName1 = supervisorName1;
    }

    public String getSupervisorName2() {
        return supervisorName2;
    }

    public void setSupervisorName2(String supervisorName2) {
        this.supervisorName2 = supervisorName2;
    }

    public String getSupervisorName3() {
        return supervisorName3;
    }

    public void setSupervisorName3(String supervisorName3) {
        this.supervisorName3 = supervisorName3;
    }

    public String getLxyz() {
        return lxyz;
    }

    public void setLxyz(String lxyz) {
        this.lxyz = lxyz;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public LocalDate getResignationDate() {
        return resignationDate;
    }

    public void setResignationDate(LocalDate resignationDate) {
        this.resignationDate = resignationDate;
    }

    public Boolean getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Boolean delete) {
        isDelete = delete;
    }

    public Boolean getIsFirstLogin() {
        return isFirstLogin;
    }

    public void setIsFirstLogin(Boolean firstLogin) {
        isFirstLogin = firstLogin;
    }


    public Double getWeight1() {
        return weight1;
    }

    public void setWeight1(Double weight1) {
        this.weight1 = weight1;
    }

    public Double getWeight2() {
        return weight2;
    }

    public void setWeight2(Double weight2) {
        this.weight2 = weight2;
    }

    public Double getWeight3() {
        return weight3;
    }

    public void setWeight3(Double weight3) {
        this.weight3 = weight3;
    }

    public Integer getFirstAdmin() {
        return firstAdmin;
    }

    public void setFirstAdmin(Integer firstAdmin) {
        this.firstAdmin = firstAdmin;
    }

    public Integer getSecondAdmin() {
        return secondAdmin;
    }

    public void setSecondAdmin(Integer secondAdmin) {
        this.secondAdmin = secondAdmin;
    }

    public Integer getSuperAdmin() {
        return superAdmin;
    }

    public void setSuperAdmin(Integer superAdmin) {
        this.superAdmin = superAdmin;
    }

    public String getFirstAdminName() {
        return firstAdminName;
    }

    public void setFirstAdminName(String firstAdminName) {
        this.firstAdminName = firstAdminName;
    }

    public String getSecondAdminName() {
        return secondAdminName;
    }

    public void setSecondAdminName(String secondAdminName) {
        this.secondAdminName = secondAdminName;
    }

    public String getSuperAdminName() {
        return superAdminName;
    }

    public void setSuperAdminName(String superAdminName) {
        this.superAdminName = superAdminName;
    }
}