package com.auu_sw3_6.Himmerland_booking_software.api.model;
import com.auu_sw3_6.Himmerland_booking_software.api.model.modelEnum.ResourceType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.validation.constraints.NotNull;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier of the resource", accessMode = Schema.AccessMode.READ_ONLY)
    private long id;

    @NotNull(message = "Name cannot be null")
    @Schema(description = "The name of the resource")
    private String name;

    @NotNull(message = "description cannot be null")
    @Schema(description = "The description of the resource")
    private String description;

    @Schema(description = "File name of the resource's picture", accessMode = Schema.AccessMode.READ_ONLY)
    private String resourcePictureFileName;

    @NotNull(message = "Type cannot be null")
    @Schema(description = "The type of the resource")
    private ResourceType type;

    @NotNull(message = "Capacity cannot be null")
    @Schema(description = "The capacity of the resource")
    private long capacity;

    @NotNull(message = "Status cannot be null")
    @Schema(description = "The status of the resource")
    private String status;

    public Resource() {
    }

    public Resource(long id, String name, String description, String resourcePictureFileName, ResourceType type, long capacity, String status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.resourcePictureFileName = resourcePictureFileName;
        this.type = type;
        this.capacity = capacity;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ResourceType getType() {
        return type;
    }

    public void setType(ResourceType type) {
        this.type = type;
    }

    public long getCapacity() {
        return capacity;
    }

    public void setCapacity(long capacity) {
        this.capacity = capacity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResourcePictureFileName() {
        return resourcePictureFileName;
    }
    
      public void setResourcePictureFileName(String resourcePictureFileName) {
        this.resourcePictureFileName = resourcePictureFileName;
      }

}