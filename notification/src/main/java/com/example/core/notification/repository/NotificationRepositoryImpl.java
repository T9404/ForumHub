package com.example.core.notification.repository;

import com.example.core.notification.repository.dto.NotificationFilterDto;
import com.example.core.notification.repository.entity.NotificationEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import lombok.AllArgsConstructor;
import org.hibernate.query.criteria.JpaSubQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;


@AllArgsConstructor
public class NotificationRepositoryImpl implements FilterNotificationRepository {
    private final EntityManager entityManager;

    @Override
    public Page<NotificationEntity> findImportantNotificationsByReceiverId(NotificationFilterDto dto, Pageable pageable) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<NotificationEntity> criteriaQuery = criteriaBuilder.createQuery(NotificationEntity.class);
        Root<NotificationEntity> root = criteriaQuery.from(NotificationEntity.class);

        Predicate predicate = buildPredicate(dto, criteriaBuilder, root);
        criteriaQuery.where(predicate);

        List<Order> orderList = new ArrayList<>();
        orderList.add(criteriaBuilder.asc(root.get("isRead")));
        orderList.add(criteriaBuilder.desc(root.get("createdAt")));
        criteriaQuery.orderBy(orderList);

        List<NotificationEntity> resultList = entityManager.createQuery(criteriaQuery)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        int totalElements = countTotalElements(dto);

        return new PageImpl<>(resultList, pageable, totalElements);
    }

    private Predicate buildPredicate(NotificationFilterDto dto, CriteriaBuilder criteriaBuilder, Root<NotificationEntity> root) {
        List<Predicate> predicates = new ArrayList<>();

        predicates.add(criteriaBuilder.equal(root.get("receiverId"), dto.receiverId()));
        predicates.add(criteriaBuilder.isTrue(root.get("isImportant")));

        if (dto.content() != null && !dto.content().isEmpty()) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("content")), "%" + dto.content().toLowerCase() + "%"));
        }

        if (dto.title() != null && !dto.title().isEmpty()) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + dto.title().toLowerCase() + "%"));
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    private int countTotalElements(NotificationFilterDto dto) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<NotificationEntity> root = countQuery.from(NotificationEntity.class);

        Predicate predicates = buildPredicate(dto, criteriaBuilder, root);
        countQuery.where(predicates);

        countQuery.select(criteriaBuilder.count(root));
        return entityManager.createQuery(countQuery).getSingleResult().intValue();
    }

}
