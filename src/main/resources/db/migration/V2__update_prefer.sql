alter table prefer
    add constraint if not exists prefer_quotation_user_unique unique (quotation_id, user_id);