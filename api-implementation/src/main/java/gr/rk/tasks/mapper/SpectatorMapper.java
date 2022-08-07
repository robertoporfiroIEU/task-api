package gr.rk.tasks.mapper;

import gr.rk.tasks.V1.dto.PaginatedSpectatorsDTO;
import gr.rk.tasks.V1.dto.SpectatorDTO;
import gr.rk.tasks.entity.Spectator;
import gr.rk.tasks.security.UserPrincipal;
import gr.rk.tasks.util.Util;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring", imports =  { Util.class, UUID.class })
public abstract class SpectatorMapper {

    @Autowired
    protected UserPrincipal userPrincipal;

    public PaginatedSpectatorsDTO toPaginatedSpectatorsDTO(Page<Spectator> spectatorEntity) {
        return new PaginatedSpectatorsDTO()
                .content(toSpectatorDTOList(spectatorEntity))
                .totalElements((int)spectatorEntity.getTotalElements());
    }

    @Mapping(ignore = true, target = "identifier")
    @Mapping(ignore = true, target = "createdAt")
    @Mapping(ignore = true, target = "task")
    @Mapping(target = "user", source = "user")
    @Mapping(target = "group", source = "group")
    @Mapping(target = "applicationUser", expression = "java(userPrincipal.getClientName())")
    public abstract Spectator toSpectator(SpectatorDTO spectatorDTO);

    @Mapping(target = "identifier", expression = "java(UUID.fromString(spectator.getIdentifier()))")
    @Mapping(target = "createdAt", expression = "java(Util.toDateISO8601WithTimeZone(spectator.getCreatedAt()))")
    @Mapping(target = "user", source = "user")
    @Mapping(target = "group", source = "group")
    public abstract SpectatorDTO toSpectatorDTO(Spectator spectator);

    protected abstract List<SpectatorDTO> toSpectatorDTOList(Page<Spectator> spectators);
}
