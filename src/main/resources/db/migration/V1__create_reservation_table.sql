create table reservation(
                      id uuid primary key,
                      client_id varchar(255) not null,
                      start_date DATE not null,
                      end_date DATE not null,
                      created_at TIMESTAMP not null
);

create index start_date_idx on reservation(
                                           start_date asc
    )