package com.harustore.backend.domain.store.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.harustore.backend.domain.store.entity.FavoriteStore;


@JsonPropertyOrder({
        "favoriteId",
        "storeId",
        "storeName",
        "description",
        "address",
        "phoneNumber",
        "operatingHours",
        "isDefault"
})
public class FavoriteStoreResponse {
    private final Long favoriteId;
    private final Long storeId;
    private final String storeName;
    private final String description;
    private final String address;
    private final String phoneNumber;
    private final String operatingHours;
    private final boolean isDefault;

    public FavoriteStoreResponse(FavoriteStore favoriteStore) {
        this.favoriteId = favoriteStore.getId();
        this.storeId = favoriteStore.getStore().getId();
        this.storeName = favoriteStore.getStore().getName();
        this.description = favoriteStore.getStore().getDescription();
        this.address = favoriteStore.getStore().getAddress();
        this.phoneNumber = favoriteStore.getStore().getPhoneNumber();
        this.operatingHours = favoriteStore.getStore().getOperatingHours();
        this.isDefault = favoriteStore.isDefault();

    }

    public Long getFavoriteId() {return favoriteId;}
    public Long getStoreId() {return storeId;}
    public String getStoreName() {return storeName;}
    public String getDescription() {return description;}
    public String getAddress() {return address;}
    public String getPhoneNumber() {return phoneNumber;}
    public String getOperatingHours() {return operatingHours;}
    public boolean isDefault() {return isDefault;}
}
