package com.hexagonal.reference.domain

import com.hexagonal.persistence.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "references_table")
class Reference(
    @Column(nullable = false, length = 100, comment = "제목")
    var title: String,

    @Column(nullable = false, length = 2_048, comment = "내용")
    var content: String,
) : BaseEntity() {

    fun update(title: String, content: String) {
        this.title = title
        this.content = content
    }

}
