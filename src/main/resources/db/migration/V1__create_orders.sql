create table orders (
    id          uuid                     not null,
    customer_id varchar(64)              not null,
    status      varchar(20)              not null,
    created_at  timestamp with time zone not null,
    constraint pk_orders primary key (id)
);

create table order_items (
    order_id   uuid           not null,
    product_id varchar(64)    not null,
    quantity   integer        not null,
    unit_price numeric(19, 2) not null,
    constraint fk_order_items_order foreign key (order_id) references orders (id),
    constraint ck_order_items_quantity check (quantity > 0),
    constraint ck_order_items_unit_price check (unit_price >= 0)
);

create index idx_order_items_order_id on order_items (order_id);
create index idx_orders_created_at on orders (created_at);
