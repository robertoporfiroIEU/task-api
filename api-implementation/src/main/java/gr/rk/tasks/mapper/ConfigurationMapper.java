package gr.rk.tasks.mapper;

import gr.rk.tasks.V1.dto.ApplicationConfigurationDTO;
import gr.rk.tasks.entity.Configuration;
import gr.rk.tasks.security.UserPrincipal;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;


@Mapper(componentModel = "spring", collectionMappingStrategy = CollectionMappingStrategy.SETTER_PREFERRED)
public abstract class ConfigurationMapper {

    @Autowired
    protected UserPrincipal userPrincipal;

    @Mapping(ignore = true, target = "updatedAt")
    @Mapping(ignore = true, target = "createdAt")
    @Mapping(ignore = true, target = "projects")
    @Mapping(target = "configurationName", source = "configurationName")
    @Mapping(target = "configurationLabel", source = "configurationLabel")
    @Mapping(target = "configurationValue", source = "configurationValue")
    @Mapping(target = "color", source = "color")
    @Mapping(target = "icon", source = "icon")
    @Mapping(target = "applicationUser", expression = "java(userPrincipal.getApplicationUser())")
    public abstract Configuration toConfiguration(ApplicationConfigurationDTO applicationConfigurationDTO);

    public abstract ApplicationConfigurationDTO toApplicationConfigurationDTO(Configuration configuration);

    public abstract Set<ApplicationConfigurationDTO> toApplicationConfigurationDTOSet(Set<Configuration> configurations);

}
