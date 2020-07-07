CREATE TABLE public.videos
(
    id serial,
    title character varying(255) COLLATE pg_catalog."default",
    url character varying(255) unique COLLATE pg_catalog."default",
    upload_url character varying(255) unique COLLATE pg_catalog."default",
    file_path character varying(255),
    vid_views bigint,
    status character varying(255) COLLATE pg_catalog."default",
    description text COLLATE pg_catalog."default",
    length_sec int,
    quality_label character varying(255),
    scheduled_time timestamp without time zone,
    download_start_time timestamp without time zone,
    download_completed_time timestamp without time zone,
    upload_start_time timestamp without time zone,
    upload_completed_time timestamp without time zone,
    cleanup_time timestamp without time zone,
    notes text,
    client_registration character varying(255),
    principal_name character varying(255),
    CONSTRAINT videos_pkey PRIMARY KEY (id)
)
TABLESPACE pg_default;