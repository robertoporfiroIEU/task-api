package gr.rk.tasks.mapper;

import gr.rk.tasks.V1.dto.ProjectConfigurationDTO;
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
    @Mapping(ignore = true, target = "project")
    @Mapping(target = "configurationName", source = "configurationName")
    @Mapping(target = "configurationLabel", source = "configurationLabel")
    @Mapping(target = "configurationValue", source = "configurationValue")
    @Mapping(target = "color", source = "color")
    @Mapping(target = "icon", source = "icon")
    @Mapping(target = "applicationUser", expression = "java(userPrincipal.getClientName())")
    public abstract Configuration toConfiguration(ProjectConfigurationDTO applicationConfigurationDTO);

    public abstract ProjectConfigurationDTO toApplicationConfigurationDTO(Configuration configuration);

    public abstract Set<ProjectConfigurationDTO> toApplicationConfigurationDTOSet(Set<Configuration> configurations);

}
