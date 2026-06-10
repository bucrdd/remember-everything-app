package com.niuma.remembereverythingapp.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import com.niuma.remembereverythingapp.entity.base.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

@Entity
@Table(name = "`user`")
class User(
  @Column(name = "username", nullable = false)
  private var username: String,
  @Column(name = "password", nullable = false)
  private var password: String,
  private var roles: Set<String> = setOf(),
) : BaseEntity<Long>(), UserDetails {

  override fun getUsername(): String = username

  @JsonIgnore
  override fun getPassword(): String = password

  //  @JsonIgnore
  override fun getAuthorities(): Collection<GrantedAuthority> =
    roles.map { SimpleGrantedAuthority(it) }

  @JsonIgnore
  override fun isAccountNonExpired(): Boolean = true

  @JsonIgnore
  override fun isAccountNonLocked(): Boolean = true

  @JsonIgnore
  override fun isCredentialsNonExpired(): Boolean = true

  @JsonIgnore
  override fun isEnabled(): Boolean = true
}