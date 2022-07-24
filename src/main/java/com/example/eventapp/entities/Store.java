package com.example.eventapp.entities;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 店舗

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "store")
@Entity
public class Store {

    // id
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	//イベントスペースのリスト
	@OneToMany(mappedBy = "store", fetch = FetchType.EAGER)
	private List<EventSpace> eventSpaces;
	
	// 店舗名
	@Column(name = "name", nullable = false)
	private String name;
	
	// パスワード
	@Column(name = "password", nullable = false)
	private String password;
	
	// ロール（役割）
	@Column(name = "roles", length = 120)
	private String roles;
	
	// 有効フラグ
	@Column(name = "enable_flag", nullable = false)
	private Boolean enable;
	
	//作成日時
    @Column(name = "createdAt", nullable = false, updatable = false, insertable = false, 
    columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private ZonedDateTime createdAt;
	
	// 更新日時
    @Column(name = "updatedAt", nullable = false, updatable = false, insertable = false, 
    columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private ZonedDateTime updatedAt;
    
    @Override
    public boolean equals(Object o) {
        if(this == o){
            return true;
        }
        if (!(o instanceof Store)) {
            return false;
        }
        return false;
    }
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Long.hashCode(id);
		result = prime * result + Objects.hashCode(name);
		result = prime * result + Objects.hashCode(password);
		result = prime * result + Objects.hashCode(roles);
		result = prime * result + Boolean.hashCode(enable);
		
		return result;
	}
}
