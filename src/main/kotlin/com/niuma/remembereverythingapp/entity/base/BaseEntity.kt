package com.niuma.remembereverythingapp.entity.base


import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import java.io.Serializable

@MappedSuperclass
abstract class BaseEntity<ID : Serializable> : Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  open var id: ID? = null
}