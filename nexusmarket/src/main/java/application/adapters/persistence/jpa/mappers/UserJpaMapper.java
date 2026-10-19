package application.adapters.persistence.jpa.mappers;

import application.adapters.persistence.jpa.entities.UserJpaEntity;
import application.domain.models.User;
import application.domain.valueobjects.SystemRole;
import application.domain.valueobjects.UserStatus;

/** User Domain Model <-> UserJpaEntity. The raw password never reaches the entity. */
public final class UserJpaMapper {

    private UserJpaMapper() {
    }

    public static UserJpaEntity toEntity(User domain) {
        if (domain == null) {
            return null;
        }
        UserJpaEntity entity = new UserJpaEntity();
        entity.setId(domain.getId());
        entity.setIdentification(domain.getIdentification());
        entity.setName(domain.getName());
        entity.setEmail(domain.getEmail());
        entity.setRole(domain.getRole().getCode());
        entity.setPasswordHash(domain.getPasswordHash());
        entity.setStatus(domain.getStatus().getCode());
        entity.setAuthTokenVersion(domain.getAuthTokenVersion());
        entity.setBuyer(BuyerJpaMapper.ref(domain.getBuyer()));
        entity.setSeller(SellerJpaMapper.ref(domain.getSeller()));
        return entity;
    }

    /** Full user, including its buyer / seller profile. */
    public static User toDomain(UserJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        User domain = toShallowDomain(entity);
        domain.setBuyer(BuyerJpaMapper.toDomain(entity.getBuyer()));
        domain.setSeller(SellerJpaMapper.toDomain(entity.getSeller()));
        return domain;
    }

    /** User without buyer / seller profile (enough to identify who performed an action). */
    public static User toShallowDomain(UserJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        User domain = new User();
        domain.setId(entity.getId());
        domain.setIdentification(entity.getIdentification());
        domain.setName(entity.getName());
        domain.setEmail(entity.getEmail());
        domain.setRole(SystemRole.fromCode(entity.getRole()));
        domain.setPasswordHash(entity.getPasswordHash());
        domain.setStatus(UserStatus.fromCode(entity.getStatus()));
        domain.setAuthTokenVersion(entity.getAuthTokenVersion());
        return domain;
    }

    /** Identifier-only entity used to reference a user from another aggregate. */
    public static UserJpaEntity ref(User domain) {
        if (domain == null || domain.getId() == null) {
            return null;
        }
        UserJpaEntity entity = new UserJpaEntity();
        entity.setId(domain.getId());
        return entity;
    }
}
