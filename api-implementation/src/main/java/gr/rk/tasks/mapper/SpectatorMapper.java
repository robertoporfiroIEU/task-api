package gr.rk.tasks.mapper;

import gr.rk.tasks.V1.dto.SpectatorDTO;
import gr.rk.tasks.entity.Spectator;
import gr.rk.tasks.security.UserPrincipal;
import gr.rk.tasks.util.Util;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring", imports =  { Util.class, UUID.class }, uses = { GroupMapper.class, UserMapper.class })
public abstract class SpectatorMapper {

    @Autowired
    protected UserPrincipal userPrincipal;

    public Page<SpectatorDTO> toPageSpectatorDTO(Page<Spectator> spectatorEntity) {
        return new PageImpl<>(toSpectatorDTOList(spectatorEntity), spectatorEntity.getPageable(), spectatorEntity.getTotalElements());
    }

    @Mapping(ignore = true, target = "identifier")
    @Mapping(ignore = true, target = "createdAt")
    @Mapping(ignore = true, target = "task")
    @Mapping(target = "user", source = "user", qualifiedByName = "toUser")
    @Mapping(target = "group", source = "group", qualifiedByName = "toGroup")
    @Mapping(target = "applicationUser", expression = "java(userPrincipal.getApplicationUser())")
    public abstract Spectator toSpectator(SpectatorDTO spectatorDTO);

    @Mapping(target = "identifier", expression = "java(UUID.fromString(spectator.getIdentifier()))")
    @Mapping(target = "creationDate", expression = "java(Util.toDateISO8601WithTimeZone(spectator.getCreatedAt()))")
    @Mapping(target = "user", source = "user", qualifiedByName = "toUserDTO")
    public abstract SpectatorDTO toSpectatorDTO(Spectator spectator);

    protected abstract List<SpectatorDTO> toSpectatorDTOList(Page<Spectator> spectators);
}
