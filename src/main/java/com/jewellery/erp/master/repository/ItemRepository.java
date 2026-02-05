package com.jewellery.erp.master.repository;

import com.jewellery.erp.master.entity.Item;
import org.hibernate.Session;
import org.hibernate.query.Query;
import java.util.Optional;

public class ItemRepository extends BaseMasterRepository<Item> {

    public ItemRepository() {
        super(Item.class);
    }

    public Optional<Item> findByNameMetalPurity(Session session, String name, Long metalId, Long purityId) {
        String hql = "FROM Item WHERE name = :name AND metalId = :metalId AND purityId = :purityId";
        Query<Item> query = session.createQuery(hql, Item.class);
        query.setParameter("name", name);
        query.setParameter("metalId", metalId);
        query.setParameter("purityId", purityId);
        return query.uniqueResultOptional();
    }
}
