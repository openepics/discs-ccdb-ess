--
-- PostgreSQL database dump
--

-- Dumped from database version 9.3.4
-- Dumped by pg_dump version 9.3.4
-- Started on 2014-06-03 13:27:42 CEST

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- TOC entry 217 (class 3079 OID 11789)
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- TOC entry 2316 (class 0 OID 0)
-- Dependencies: 217
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 172 (class 1259 OID 41855)
-- Name: alignment_artifact; Type: TABLE; Schema: public; Owner: discs_ccdb; Tablespace: 
--

CREATE TABLE alignment_artifact (
    artifact_id integer NOT NULL,
    description character varying(255),
    is_internal boolean NOT NULL,
    modified_at timestamp without time zone NOT NULL,
    modified_by character varying(255) NOT NULL,
    name character varying(255) NOT NULL,
    uri text NOT NULL,
    alignment_record integer NOT NULL
);


ALTER TABLE public.alignment_artifact OWNER TO discs_ccdb;

--
-- TOC entry 171 (class 1259 OID 41853)
-- Name: alignment_artifact_artifact_id_seq; Type: SEQUENCE; Schema: public; Owner: discs_ccdb
--

CREATE SEQUENCE alignment_artifact_artifact_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.alignment_artifact_artifact_id_seq OWNER TO discs_ccdb;

--
-- TOC entry 2318 (class 0 OID 0)
-- Dependencies: 171
-- Name: alignment_artifact_artifact_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: discs_ccdb
--

ALTER SEQUENCE alignment_artifact_artifact_id_seq OWNED BY alignment_artifact.artifact_id;


--
-- TOC entry 174 (class 1259 OID 41866)
-- Name: alignment_property; Type: TABLE; Schema: public; Owner: discs_ccdb; Tablespace: 
--

CREATE TABLE alignment_property (
    align_prop_id integer NOT NULL,
    in_repository boolean NOT NULL,
    modified_at timestamp without time zone NOT NULL,
    modified_by character varying(255) NOT NULL,
    prop_value text,
    version integer NOT NULL,
    alignment_record integer NOT NULL,
    property integer NOT NULL
);


ALTER TABLE public.alignment_property OWNER TO discs_ccdb;

--
-- TOC entry 173 (class 1259 OID 41864)
-- Name: alignment_property_align_prop_id_seq; Type: SEQUENCE; Schema: public; Owner: discs_ccdb
--

CREATE SEQUENCE alignment_property_align_prop_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.alignment_property_align_prop_id_seq OWNER TO discs_ccdb;

--
-- TOC entry 2321 (class 0 OID 0)
-- Dependencies: 173
-- Name: alignment_property_align_prop_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: discs_ccdb
--

ALTER SEQUENCE alignment_property_align_prop_id_seq OWNED BY alignment_property.align_prop_id;


--
-- TOC entry 176 (class 1259 OID 41877)
-- Name: alignment_record; Type: TABLE; Schema: public; Owner: discs_ccdb; Tablespace: 
--

CREATE TABLE alignment_record (
    alignment_record_id integer NOT NULL,
    alignment_date timestamp without time zone NOT NULL,
    global_pitch double precision,
    global_roll double precision,
    global_x double precision,
    global_y double precision,
    global_yaw double precision,
    global_z double precision,
    modified_at timestamp without time zone NOT NULL,
    modified_by character varying(255) NOT NULL,
    record_number character varying(255) NOT NULL,
    version integer NOT NULL,
    device integer NOT NULL,
    slot integer NOT NULL
);


ALTER TABLE public.alignment_record OWNER TO discs_ccdb;

--
-- TOC entry 175 (class 1259 OID 41875)
-- Name: alignment_record_alignment_record_id_seq; Type: SEQUENCE; Schema: public; Owner: discs_ccdb
--

CREATE SEQUENCE alignment_record_alignment_record_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.alignment_record_alignment_record_id_seq OWNER TO discs_ccdb;

--
-- TOC entry 2324 (class 0 OID 0)
-- Dependencies: 175
-- Name: alignment_record_alignment_record_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: discs_ccdb
--

ALTER SEQUENCE alignment_record_alignment_record_id_seq OWNED BY alignment_record.alignment_record_id;


--
-- TOC entry 178 (class 1259 OID 41888)
-- Name: audit_record; Type: TABLE; Schema: public; Owner: discs_ccdb; Tablespace: 
--

CREATE TABLE audit_record (
    audit_record_id integer NOT NULL,
    entity_key character varying(255),
    entity_type character varying(255),
    entry text NOT NULL,
    log_time timestamp without time zone NOT NULL,
    oper character varying(255) NOT NULL,
    "user" character varying(255) NOT NULL
);


ALTER TABLE public.audit_record OWNER TO discs_ccdb;

--
-- TOC entry 177 (class 1259 OID 41886)
-- Name: audit_record_audit_record_id_seq; Type: SEQUENCE; Schema: public; Owner: discs_ccdb
--

CREATE SEQUENCE audit_record_audit_record_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.audit_record_audit_record_id_seq OWNER TO discs_ccdb;

--
-- TOC entry 2327 (class 0 OID 0)
-- Dependencies: 177
-- Name: audit_record_audit_record_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: discs_ccdb
--

ALTER SEQUENCE audit_record_audit_record_id_seq OWNED BY audit_record.audit_record_id;


--
-- TOC entry 180 (class 1259 OID 41899)
-- Name: component_type; Type: TABLE; Schema: public; Owner: discs_ccdb; Tablespace: 
--

CREATE TABLE component_type (
    component_type_id integer NOT NULL,
    description character varying(255),
    modified_at timestamp without time zone NOT NULL,
    modified_by character varying(255) NOT NULL,
    name character varying(255) NOT NULL,
    version integer NOT NULL,
    super_component_type integer
);


ALTER TABLE public.component_type OWNER TO discs_ccdb;

--
-- TOC entry 179 (class 1259 OID 41897)
-- Name: component_type_component_type_id_seq; Type: SEQUENCE; Schema: public; Owner: discs_ccdb
--

CREATE SEQUENCE component_type_component_type_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.component_type_component_type_id_seq OWNER TO discs_ccdb;

--
-- TOC entry 2330 (class 0 OID 0)
-- Dependencies: 179
-- Name: component_type_component_type_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: discs_ccdb
--

ALTER SEQUENCE component_type_component_type_id_seq OWNED BY component_type.component_type_id;


--
-- TOC entry 182 (class 1259 OID 41910)
-- Name: comptype_artifact; Type: TABLE; Schema: public; Owner: discs_ccdb; Tablespace: 
--

CREATE TABLE comptype_artifact (
    artifact_id integer NOT NULL,
    description character varying(255) NOT NULL,
    is_internal boolean NOT NULL,
    modified_at timestamp without time zone NOT NULL,
    modified_by character varying(255) NOT NULL,
    name character varying(255) NOT NULL,
    uri text NOT NULL,
    component_type integer NOT NULL
);


ALTER TABLE public.comptype_artifact OWNER TO discs_ccdb;

--
-- TOC entry 181 (class 1259 OID 41908)
-- Name: comptype_artifact_artifact_id_seq; Type: SEQUENCE; Schema: public; Owner: discs_ccdb
--

CREATE SEQUENCE comptype_artifact_artifact_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.comptype_artifact_artifact_id_seq OWNER TO discs_ccdb;

--
-- TOC entry 2333 (class 0 OID 0)
-- Dependencies: 181
-- Name: comptype_artifact_artifact_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: discs_ccdb
--

ALTER SEQUENCE comptype_artifact_artifact_id_seq OWNED BY comptype_artifact.artifact_id;


--
-- TOC entry 184 (class 1259 OID 41921)
-- Name: comptype_asm; Type: TABLE; Schema: public; Owner: discs_ccdb; Tablespace: 
--

CREATE TABLE comptype_asm (
    comptype_asm_id integer NOT NULL,
    child_position character varying(255) NOT NULL,
    description character varying(255),
    modified_at timestamp without time zone NOT NULL,
    modified_by character varying(255) NOT NULL,
    version integer NOT NULL,
    child_type integer NOT NULL,
    parent_type integer NOT NULL
);


ALTER TABLE public.comptype_asm OWNER TO discs_ccdb;

--
-- TOC entry 183 (class 1259 OID 41919)
-- Name: comptype_asm_comptype_asm_id_seq; Type: SEQUENCE; Schema: public; Owner: discs_ccdb
--

CREATE SEQUENCE comptype_asm_comptype_asm_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.comptype_asm_comptype_asm_id_seq OWNER TO discs_ccdb;

--
-- TOC entry 2336 (class 0 OID 0)
-- Dependencies: 183
-- Name: comptype_asm_comptype_asm_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: discs_ccdb
--

ALTER SEQUENCE comptype_asm_comptype_asm_id_seq OWNED BY comptype_asm.comptype_asm_id;


--
-- TOC entry 186 (class 1259 OID 41932)
-- Name: comptype_property; Type: TABLE; Schema: public; Owner: discs_ccdb; Tablespace: 
--

CREATE TABLE comptype_property (
    ctype_prop_id integer NOT NULL,
    in_repository boolean NOT NULL,
    modified_at timestamp without time zone NOT NULL,
    modified_by character varying(255) NOT NULL,
    prop_value text,
    type character varying(255),
    version integer NOT NULL,
    component_type integer NOT NULL,
    property integer NOT NULL,
    unit character varying(255)
);


ALTER TABLE public.comptype_property OWNER TO discs_ccdb;

--
-- TOC entry 185 (class 1259 OID 41930)
-- Name: comptype_property_ctype_prop_id_seq; Type: SEQUENCE; Schema: public; Owner: discs_ccdb
--

CREATE SEQUENCE comptype_property_ctype_prop_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.comptype_property_ctype_prop_id_seq OWNER TO discs_ccdb;

--
-- TOC entry 2339 (class 0 OID 0)
-- Dependencies: 185
-- Name: comptype_property_ctype_prop_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: discs_ccdb
--

ALTER SEQUENCE comptype_property_ctype_prop_id_seq OWNED BY comptype_property.ctype_prop_id;


--
-- TOC entry 187 (class 1259 OID 41941)
-- Name: config; Type: TABLE; Schema: public; Owner: discs_ccdb; Tablespace: 
--

CREATE TABLE config (
    name character varying(255) NOT NULL,
    prop_value character varying(255)
);


ALTER TABLE public.config OWNER TO discs_ccdb;

--
-- TOC entry 188 (class 1259 OID 41949)
-- Name: data_type; Type: TABLE; Schema: public; Owner: discs_ccdb; Tablespace: 
--

CREATE TABLE data_type (
    data_type_id character varying(255) NOT NULL,
    description character varying(255) NOT NULL,
    modified_at timestamp without time zone NOT NULL,
    modified_by character varying(255) NOT NULL,
    scalar boolean NOT NULL,
    version integer NOT NULL
);


ALTER TABLE public.data_type OWNER TO discs_ccdb;

--
-- TOC entry 190 (class 1259 OID 41959)
-- Name: device; Type: TABLE; Schema: public; Owner: discs_ccdb; Tablespace: 
--

CREATE TABLE device (
    device_id integer NOT NULL,
    asm_description character varying(255),
    asm_position character varying(255),
    description character varying(255),
    location character varying(255),
    manuf_model character varying(255),
    manuf_serial_number character varying(255),
    manufacturer character varying(255),
    modified_at timestamp without time zone NOT NULL,
    modified_by character varying(255) NOT NULL,
    purchase_order character varying(255),
    serial_number character varying(255) NOT NULL,
    status character(1),
    version integer NOT NULL,
    asm_parent integer,
    component_type integer NOT NULL,
    uuid character varying(255)
);


ALTER TABLE public.device OWNER TO discs_ccdb;

--
-- TOC entry 192 (class 1259 OID 41970)
-- Name: device_artifact; Type: TABLE; Schema: public; Owner: discs_ccdb; Tablespace: 
--

CREATE TABLE device_artifact (
    artifact_id integer NOT NULL,
    description character varying(255) NOT NULL,
    is_internal boolean NOT NULL,
    modified_at timestamp without time zone NOT NULL,
    modified_by character varying(255) NOT NULL,
    name character varying(255) NOT NULL,
    uri text NOT NULL,
    device integer NOT NULL
);


ALTER TABLE public.device_artifact OWNER TO discs_ccdb;

--
-- TOC entry 191 (class 1259 OID 41968)
-- Name: device_artifact_artifact_id_seq; Type: SEQUENCE; Schema: public; Owner: discs_ccdb
--

CREATE SEQUENCE device_artifact_artifact_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.device_artifact_artifact_id_seq OWNER TO discs_ccdb;

--
-- TOC entry 2345 (class 0 OID 0)
-- Dependencies: 191
-- Name: device_artifact_artifact_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: discs_ccdb
--

ALTER SEQUENCE device_artifact_artifact_id_seq OWNED BY device_artifact.artifact_id;


--
-- TOC entry 189 (class 1259 OID 41957)
-- Name: device_device_id_seq; Type: SEQUENCE; Schema: public; Owner: discs_ccdb
--

CREATE SEQUENCE device_device_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.device_device_id_seq OWNER TO discs_ccdb;

--
-- TOC entry 2347 (class 0 OID 0)
-- Dependencies: 189
-- Name: device_device_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: discs_ccdb
--

ALTER SEQUENCE device_device_id_seq OWNED BY device.device_id;


--
-- TOC entry 194 (class 1259 OID 41981)
-- Name: device_property; Type: TABLE; Schema: public; Owner: discs_ccdb; Tablespace: 
--

CREATE TABLE device_property (
    dev_prop_id integer NOT NULL,
    in_repository boolean NOT NULL,
    modified_at timestamp without time zone NOT NULL,
    modified_by character varying(255) NOT NULL,
    prop_value text,
    version integer NOT NULL,
    device integer NOT NULL,
    property integer NOT NULL,
    unit character varying(255)
);


ALTER TABLE public.device_property OWNER TO discs_ccdb;

--
-- TOC entry 193 (class 1259 OID 41979)
-- Name: device_property_dev_prop_id_seq; Type: SEQUENCE; Schema: public; Owner: discs_ccdb
--

CREATE SEQUENCE device_property_dev_prop_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.device_property_dev_prop_id_seq OWNER TO discs_ccdb;

--
-- TOC entry 2350 (class 0 OID 0)
-- Dependencies: 193
-- Name: device_property_dev_prop_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: discs_ccdb
--

ALTER SEQUENCE device_property_dev_prop_id_seq OWNED BY device_property.dev_prop_id;


--
-- TOC entry 196 (class 1259 OID 41992)
-- Name: installation_artifact; Type: TABLE; Schema: public; Owner: discs_ccdb; Tablespace: 
--

CREATE TABLE installation_artifact (
    artifact_id integer NOT NULL,
    description character varying(255),
    is_internal boolean NOT NULL,
    modified_at timestamp without time zone NOT NULL,
    modified_by character varying(255) NOT NULL,
    name character varying(255) NOT NULL,
    uri text NOT NULL,
    installation_record integer NOT NULL
);


ALTER TABLE public.installation_artifact OWNER TO discs_ccdb;

--
-- TOC entry 195 (class 1259 OID 41990)
-- Name: installation_artifact_artifact_id_seq; Type: SEQUENCE; Schema: public; Owner: discs_ccdb
--

CREATE SEQUENCE installation_artifact_artifact_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.installation_artifact_artifact_id_seq OWNER TO discs_ccdb;

--
-- TOC entry 2353 (class 0 OID 0)
-- Dependencies: 195
-- Name: installation_artifact_artifact_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: discs_ccdb
--

ALTER SEQUENCE installation_artifact_artifact_id_seq OWNED BY installation_artifact.artifact_id;


--
-- TOC entry 198 (class 1259 OID 42003)
-- Name: installation_record; Type: TABLE; Schema: public; Owner: discs_ccdb; Tablespace: 
--

CREATE TABLE installation_record (
    installation_record_id integer NOT NULL,
    install_date date NOT NULL,
    modified_at timestamp without time zone NOT NULL,
    modified_by character varying(255) NOT NULL,
    notes text,
    record_number character varying(255) NOT NULL,
    uninstall_date date,
    version integer NOT NULL,
    device integer NOT NULL,
    slot integer NOT NULL,
    uuid character varying(255)
);


ALTER TABLE public.installation_record OWNER TO discs_ccdb;

--
-- TOC entry 197 (class 1259 OID 42001)
-- Name: installation_record_installation_record_id_seq; Type: SEQUENCE; Schema: public; Owner: discs_ccdb
--

CREATE SEQUENCE installation_record_installation_record_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.installation_record_installation_record_id_seq OWNER TO discs_ccdb;

--
-- TOC entry 2356 (class 0 OID 0)
-- Dependencies: 197
-- Name: installation_record_installation_record_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: discs_ccdb
--

ALTER SEQUENCE installation_record_installation_record_id_seq OWNED BY installation_record.installation_record_id;


--
-- TOC entry 200 (class 1259 OID 42014)
-- Name: privilege; Type: TABLE; Schema: public; Owner: discs_ccdb; Tablespace: 
--

CREATE TABLE privilege (
    privilege_id integer NOT NULL,
    oper character varying(255) NOT NULL,
    resource character varying(255) NOT NULL,
    role character varying(255) NOT NULL
);


ALTER TABLE public.privilege OWNER TO discs_ccdb;

--
-- TOC entry 199 (class 1259 OID 42012)
-- Name: privilege_privilege_id_seq; Type: SEQUENCE; Schema: public; Owner: discs_ccdb
--

CREATE SEQUENCE privilege_privilege_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.privilege_privilege_id_seq OWNER TO discs_ccdb;

--
-- TOC entry 2359 (class 0 OID 0)
-- Dependencies: 199
-- Name: privilege_privilege_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: discs_ccdb
--

ALTER SEQUENCE privilege_privilege_id_seq OWNED BY privilege.privilege_id;


--
-- TOC entry 202 (class 1259 OID 42025)
-- Name: property; Type: TABLE; Schema: public; Owner: discs_ccdb; Tablespace: 
--

CREATE TABLE property (
    property_id integer NOT NULL,
    association character varying(255) NOT NULL,
    description character varying(255) NOT NULL,
    modified_at timestamp without time zone NOT NULL,
    modified_by character varying(255) NOT NULL,
    name character varying(255) NOT NULL,
    version integer NOT NULL,
    data_type character varying(255) NOT NULL,
    unit character varying(255)
);


ALTER TABLE public.property OWNER TO discs_ccdb;

--
-- TOC entry 201 (class 1259 OID 42023)
-- Name: property_property_id_seq; Type: SEQUENCE; Schema: public; Owner: discs_ccdb
--

CREATE SEQUENCE property_property_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.property_property_id_seq OWNER TO discs_ccdb;

--
-- TOC entry 2362 (class 0 OID 0)
-- Dependencies: 201
-- Name: property_property_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: discs_ccdb
--

ALTER SEQUENCE property_property_id_seq OWNED BY property.property_id;


--
-- TOC entry 203 (class 1259 OID 42034)
-- Name: role; Type: TABLE; Schema: public; Owner: discs_ccdb; Tablespace: 
--

CREATE TABLE role (
    role_id character varying(255) NOT NULL,
    description character varying(255) NOT NULL,
    version integer NOT NULL
);


ALTER TABLE public.role OWNER TO discs_ccdb;

--
-- TOC entry 205 (class 1259 OID 42044)
-- Name: slot; Type: TABLE; Schema: public; Owner: discs_ccdb; Tablespace: 
--

CREATE TABLE slot (
    slot_id integer NOT NULL,
    asm_comment character varying(255),
    asm_position character varying(255),
    beamline_position double precision,
    comment character varying(255),
    description character varying(255),
    global_pitch double precision,
    global_roll double precision,
    global_x double precision,
    global_y double precision,
    global_yaw double precision,
    global_z double precision,
    is_hosting_slot boolean NOT NULL,
    modified_at timestamp without time zone NOT NULL,
    modified_by character varying(255) NOT NULL,
    name character varying(255) NOT NULL,
    version integer NOT NULL,
    asm_slot integer,
    component_type integer NOT NULL,
    uuid character varying(255)
);


ALTER TABLE public.slot OWNER TO discs_ccdb;

--
-- TOC entry 207 (class 1259 OID 42055)
-- Name: slot_artifact; Type: TABLE; Schema: public; Owner: discs_ccdb; Tablespace: 
--

CREATE TABLE slot_artifact (
    artifact_id integer NOT NULL,
    description character varying(255) NOT NULL,
    is_internal boolean NOT NULL,
    modified_at timestamp without time zone NOT NULL,
    modified_by character varying(255) NOT NULL,
    name character varying(255) NOT NULL,
    uri text NOT NULL,
    slot integer NOT NULL
);


ALTER TABLE public.slot_artifact OWNER TO discs_ccdb;

--
-- TOC entry 206 (class 1259 OID 42053)
-- Name: slot_artifact_artifact_id_seq; Type: SEQUENCE; Schema: public; Owner: discs_ccdb
--

CREATE SEQUENCE slot_artifact_artifact_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.slot_artifact_artifact_id_seq OWNER TO discs_ccdb;

--
-- TOC entry 2367 (class 0 OID 0)
-- Dependencies: 206
-- Name: slot_artifact_artifact_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: discs_ccdb
--

ALTER SEQUENCE slot_artifact_artifact_id_seq OWNED BY slot_artifact.artifact_id;


--
-- TOC entry 209 (class 1259 OID 42066)
-- Name: slot_pair; Type: TABLE; Schema: public; Owner: discs_ccdb; Tablespace: 
--

CREATE TABLE slot_pair (
    slot_pair_id integer NOT NULL,
    version integer NOT NULL,
    child_slot integer NOT NULL,
    parent_slot integer NOT NULL,
    slot_relation integer NOT NULL
);


ALTER TABLE public.slot_pair OWNER TO discs_ccdb;

--
-- TOC entry 208 (class 1259 OID 42064)
-- Name: slot_pair_slot_pair_id_seq; Type: SEQUENCE; Schema: public; Owner: discs_ccdb
--

CREATE SEQUENCE slot_pair_slot_pair_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.slot_pair_slot_pair_id_seq OWNER TO discs_ccdb;

--
-- TOC entry 2370 (class 0 OID 0)
-- Dependencies: 208
-- Name: slot_pair_slot_pair_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: discs_ccdb
--

ALTER SEQUENCE slot_pair_slot_pair_id_seq OWNED BY slot_pair.slot_pair_id;


--
-- TOC entry 211 (class 1259 OID 42074)
-- Name: slot_property; Type: TABLE; Schema: public; Owner: discs_ccdb; Tablespace: 
--

CREATE TABLE slot_property (
    slot_prop_id integer NOT NULL,
    in_repository boolean NOT NULL,
    modified_at date NOT NULL,
    modified_by character varying(255) NOT NULL,
    prop_value text,
    version integer NOT NULL,
    property integer NOT NULL,
    slot integer NOT NULL,
    unit character varying(255)
);


ALTER TABLE public.slot_property OWNER TO discs_ccdb;

--
-- TOC entry 210 (class 1259 OID 42072)
-- Name: slot_property_slot_prop_id_seq; Type: SEQUENCE; Schema: public; Owner: discs_ccdb
--

CREATE SEQUENCE slot_property_slot_prop_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.slot_property_slot_prop_id_seq OWNER TO discs_ccdb;

--
-- TOC entry 2373 (class 0 OID 0)
-- Dependencies: 210
-- Name: slot_property_slot_prop_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: discs_ccdb
--

ALTER SEQUENCE slot_property_slot_prop_id_seq OWNED BY slot_property.slot_prop_id;


--
-- TOC entry 213 (class 1259 OID 42085)
-- Name: slot_relation; Type: TABLE; Schema: public; Owner: discs_ccdb; Tablespace: 
--

CREATE TABLE slot_relation (
    slot_relation_id integer NOT NULL,
    description character varying(255),
    iname character varying(255) NOT NULL,
    modified_at timestamp without time zone NOT NULL,
    modified_by character varying(255) NOT NULL,
    name character varying(255) NOT NULL,
    version integer NOT NULL
);


ALTER TABLE public.slot_relation OWNER TO discs_ccdb;

--
-- TOC entry 212 (class 1259 OID 42083)
-- Name: slot_relation_slot_relation_id_seq; Type: SEQUENCE; Schema: public; Owner: discs_ccdb
--

CREATE SEQUENCE slot_relation_slot_relation_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.slot_relation_slot_relation_id_seq OWNER TO discs_ccdb;

--
-- TOC entry 2376 (class 0 OID 0)
-- Dependencies: 212
-- Name: slot_relation_slot_relation_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: discs_ccdb
--

ALTER SEQUENCE slot_relation_slot_relation_id_seq OWNED BY slot_relation.slot_relation_id;


--
-- TOC entry 204 (class 1259 OID 42042)
-- Name: slot_slot_id_seq; Type: SEQUENCE; Schema: public; Owner: discs_ccdb
--

CREATE SEQUENCE slot_slot_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.slot_slot_id_seq OWNER TO discs_ccdb;

--
-- TOC entry 2378 (class 0 OID 0)
-- Dependencies: 204
-- Name: slot_slot_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: discs_ccdb
--

ALTER SEQUENCE slot_slot_id_seq OWNED BY slot.slot_id;


--
-- TOC entry 214 (class 1259 OID 42094)
-- Name: unit; Type: TABLE; Schema: public; Owner: discs_ccdb; Tablespace: 
--

CREATE TABLE unit (
    unit_id character varying(255) NOT NULL,
    base_unit_expr character varying(255),
    description character varying(255) NOT NULL,
    modified_at timestamp without time zone NOT NULL,
    modified_by character varying(255) NOT NULL,
    quantity character varying(255) NOT NULL,
    symbol character varying(255) NOT NULL,
    version integer NOT NULL
);


ALTER TABLE public.unit OWNER TO discs_ccdb;

--
-- TOC entry 170 (class 1259 OID 41845)
-- Name: user; Type: TABLE; Schema: public; Owner: discs_ccdb; Tablespace: 
--

CREATE TABLE "user" (
    user_id character varying(255) NOT NULL,
    comment character varying(255),
    email character varying(255),
    name character varying(255) NOT NULL,
    version integer NOT NULL
);


ALTER TABLE public."user" OWNER TO discs_ccdb;

--
-- TOC entry 216 (class 1259 OID 42104)
-- Name: user_role; Type: TABLE; Schema: public; Owner: discs_ccdb; Tablespace: 
--

CREATE TABLE user_role (
    user_role_id integer NOT NULL,
    candelegate boolean NOT NULL,
    comment character varying(255),
    endtime timestamp without time zone NOT NULL,
    isrolemanager boolean NOT NULL,
    starttime timestamp without time zone NOT NULL,
    version integer NOT NULL,
    role character varying(255) NOT NULL,
    "user" character varying(255) NOT NULL
);


ALTER TABLE public.user_role OWNER TO discs_ccdb;

--
-- TOC entry 215 (class 1259 OID 42102)
-- Name: user_role_user_role_id_seq; Type: SEQUENCE; Schema: public; Owner: discs_ccdb
--

CREATE SEQUENCE user_role_user_role_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.user_role_user_role_id_seq OWNER TO discs_ccdb;

--
-- TOC entry 2383 (class 0 OID 0)
-- Dependencies: 215
-- Name: user_role_user_role_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: discs_ccdb
--

ALTER SEQUENCE user_role_user_role_id_seq OWNED BY user_role.user_role_id;


--
-- TOC entry 2026 (class 2604 OID 41858)
-- Name: artifact_id; Type: DEFAULT; Schema: public; Owner: discs_ccdb
--

ALTER TABLE ONLY alignment_artifact ALTER COLUMN artifact_id SET DEFAULT nextval('alignment_artifact_artifact_id_seq'::regclass);


--
-- TOC entry 2027 (class 2604 OID 41869)
-- Name: align_prop_id; Type: DEFAULT; Schema: public; Owner: discs_ccdb
--

ALTER TABLE ONLY alignment_property ALTER COLUMN align_prop_id SET DEFAULT nextval('alignment_property_align_prop_id_seq'::regclass);


--
-- TOC entry 2028 (class 2604 OID 41880)
-- Name: alignment_record_id; Type: DEFAULT; Schema: public; Owner: discs_ccdb
--

ALTER TABLE ONLY alignment_record ALTER COLUMN alignment_record_id SET DEFAULT nextval('alignment_record_alignment_record_id_seq'::regclass);


--
-- TOC entry 2029 (class 2604 OID 41891)
-- Name: audit_record_id; Type: DEFAULT; Schema: public; Owner: discs_ccdb
--

ALTER TABLE ONLY audit_record ALTER COLUMN audit_record_id SET DEFAULT nextval('audit_record_audit_record_id_seq'::regclass);


--
-- TOC entry 2030 (class 2604 OID 41902)
-- Name: component_type_id; Type: DEFAULT; Schema: public; Owner: discs_ccdb
--

ALTER TABLE ONLY component_type ALTER COLUMN component_type_id SET DEFAULT nextval('component_type_component_type_id_seq'::regclass);


--
-- TOC entry 2031 (class 2604 OID 41913)
-- Name: artifact_id; Type: DEFAULT; Schema: public; Owner: discs_ccdb
--

ALTER TABLE ONLY comptype_artifact ALTER COLUMN artifact_id SET DEFAULT nextval('comptype_artifact_artifact_id_seq'::regclass);


--
-- TOC entry 2032 (class 2604 OID 41924)
-- Name: comptype_asm_id; Type: DEFAULT; Schema: public; Owner: discs_ccdb
--

ALTER TABLE ONLY comptype_asm ALTER COLUMN comptype_asm_id SET DEFAULT nextval('comptype_asm_comptype_asm_id_seq'::regclass);


--
-- TOC entry 2033 (class 2604 OID 41935)
-- Name: ctype_prop_id; Type: DEFAULT; Schema: public; Owner: discs_ccdb
--

ALTER TABLE ONLY comptype_property ALTER COLUMN ctype_prop_id SET DEFAULT nextval('comptype_property_ctype_prop_id_seq'::regclass);


--
-- TOC entry 2034 (class 2604 OID 41962)
-- Name: device_id; Type: DEFAULT; Schema: public; Owner: discs_ccdb
--

ALTER TABLE ONLY device ALTER COLUMN device_id SET DEFAULT nextval('device_device_id_seq'::regclass);


--
-- TOC entry 2035 (class 2604 OID 41973)
-- Name: artifact_id; Type: DEFAULT; Schema: public; Owner: discs_ccdb
--

ALTER TABLE ONLY device_artifact ALTER COLUMN artifact_id SET DEFAULT nextval('device_artifact_artifact_id_seq'::regclass);


--
-- TOC entry 2036 (class 2604 OID 41984)
-- Name: dev_prop_id; Type: DEFAULT; Schema: public; Owner: discs_ccdb
--

ALTER TABLE ONLY device_property ALTER COLUMN dev_prop_id SET DEFAULT nextval('device_property_dev_prop_id_seq'::regclass);


--
-- TOC entry 2037 (class 2604 OID 41995)
-- Name: artifact_id; Type: DEFAULT; Schema: public; Owner: discs_ccdb
--

ALTER TABLE ONLY installation_artifact ALTER COLUMN artifact_id SET DEFAULT nextval('installation_artifact_artifact_id_seq'::regclass);


--
-- TOC entry 2038 (class 2604 OID 42006)
-- Name: installation_record_id; Type: DEFAULT; Schema: public; Owner: discs_ccdb
--

ALTER TABLE ONLY installation_record ALTER COLUMN installation_record_id SET DEFAULT nextval('installation_record_installation_record_id_seq'::regclass);


--
-- TOC entry 2039 (class 2604 OID 42017)
-- Name: privilege_id; Type: DEFAULT; Schema: public; Owner: discs_ccdb
--

ALTER TABLE ONLY privilege ALTER COLUMN privilege_id SET DEFAULT nextval('privilege_privilege_id_seq'::regclass);


--
-- TOC entry 2040 (class 2604 OID 42028)
-- Name: property_id; Type: DEFAULT; Schema: public; Owner: discs_ccdb
--

ALTER TABLE ONLY property ALTER COLUMN property_id SET DEFAULT nextval('property_property_id_seq'::regclass);


--
-- TOC entry 2041 (class 2604 OID 42047)
-- Name: slot_id; Type: DEFAULT; Schema: public; Owner: discs_ccdb
--

ALTER TABLE ONLY slot ALTER COLUMN slot_id SET DEFAULT nextval('slot_slot_id_seq'::regclass);


--
-- TOC entry 2042 (class 2604 OID 42058)
-- Name: artifact_id; Type: DEFAULT; Schema: public; Owner: discs_ccdb
--

ALTER TABLE ONLY slot_artifact ALTER COLUMN artifact_id SET DEFAULT nextval('slot_artifact_artifact_id_seq'::regclass);


--
-- TOC entry 2043 (class 2604 OID 42069)
-- Name: slot_pair_id; Type: DEFAULT; Schema: public; Owner: discs_ccdb
--

ALTER TABLE ONLY slot_pair ALTER COLUMN slot_pair_id SET DEFAULT nextval('slot_pair_slot_pair_id_seq'::regclass);


--
-- TOC entry 2044 (class 2604 OID 42077)
-- Name: slot_prop_id; Type: DEFAULT; Schema: public; Owner: discs_ccdb
--

ALTER TABLE ONLY slot_property ALTER COLUMN slot_prop_id SET DEFAULT nextval('slot_property_slot_prop_id_seq'::regclass);


--
-- TOC entry 2045 (class 2604 OID 42088)
-- Name: slot_relation_id; Type: DEFAULT; Schema: public; Owner: discs_ccdb
--

ALTER TABLE ONLY slot_relation ALTER COLUMN slot_relation_id SET DEFAULT nextval('slot_relation_slot_relation_id_seq'::regclass);


--
-- TOC entry 2046 (class 2604 OID 42107)
-- Name: user_role_id; Type: DEFAULT; Schema: public; Owner: discs_ccdb
--

ALTER TABLE ONLY user_role ALTER COLUMN user_role_id SET DEFAULT nextval('user_role_user_role_id_seq'::regclass);


--
-- TOC entry 2288 (class 2613 OID 42289)
-- Name: 42289; Type: BLOB; Schema: -; Owner: discs_ccdb
--

SELECT pg_catalog.lo_create('42289');


ALTER LARGE OBJECT 42289 OWNER TO discs_ccdb;

--
-- TOC entry 2289 (class 2613 OID 42290)
-- Name: 42290; Type: BLOB; Schema: -; Owner: discs_ccdb
--

SELECT pg_catalog.lo_create('42290');


ALTER LARGE OBJECT 42290 OWNER TO discs_ccdb;

--
-- TOC entry 2290 (class 2613 OID 42291)
-- Name: 42291; Type: BLOB; Schema: -; Owner: discs_ccdb
--

SELECT pg_catalog.lo_create('42291');


ALTER LARGE OBJECT 42291 OWNER TO discs_ccdb;

--
-- TOC entry 2291 (class 2613 OID 42299)
-- Name: 42299; Type: BLOB; Schema: -; Owner: discs_ccdb
--

SELECT pg_catalog.lo_create('42299');


ALTER LARGE OBJECT 42299 OWNER TO discs_ccdb;

--
-- TOC entry 2292 (class 2613 OID 42300)
-- Name: 42300; Type: BLOB; Schema: -; Owner: discs_ccdb
--

SELECT pg_catalog.lo_create('42300');


ALTER LARGE OBJECT 42300 OWNER TO discs_ccdb;

--
-- TOC entry 2293 (class 2613 OID 42302)
-- Name: 42302; Type: BLOB; Schema: -; Owner: discs_ccdb
--

SELECT pg_catalog.lo_create('42302');


ALTER LARGE OBJECT 42302 OWNER TO discs_ccdb;

--
-- TOC entry 2294 (class 2613 OID 42303)
-- Name: 42303; Type: BLOB; Schema: -; Owner: discs_ccdb
--

SELECT pg_catalog.lo_create('42303');


ALTER LARGE OBJECT 42303 OWNER TO discs_ccdb;

--
-- TOC entry 2295 (class 2613 OID 42307)
-- Name: 42307; Type: BLOB; Schema: -; Owner: discs_ccdb
--

SELECT pg_catalog.lo_create('42307');


ALTER LARGE OBJECT 42307 OWNER TO discs_ccdb;

--
-- TOC entry 2296 (class 2613 OID 42308)
-- Name: 42308; Type: BLOB; Schema: -; Owner: discs_ccdb
--

SELECT pg_catalog.lo_create('42308');


ALTER LARGE OBJECT 42308 OWNER TO discs_ccdb;

--
-- TOC entry 2297 (class 2613 OID 50498)
-- Name: 50498; Type: BLOB; Schema: -; Owner: discs_ccdb
--

SELECT pg_catalog.lo_create('50498');


ALTER LARGE OBJECT 50498 OWNER TO discs_ccdb;

--
-- TOC entry 2298 (class 2613 OID 50499)
-- Name: 50499; Type: BLOB; Schema: -; Owner: discs_ccdb
--

SELECT pg_catalog.lo_create('50499');


ALTER LARGE OBJECT 50499 OWNER TO discs_ccdb;

--
-- TOC entry 2299 (class 2613 OID 50500)
-- Name: 50500; Type: BLOB; Schema: -; Owner: discs_ccdb
--

SELECT pg_catalog.lo_create('50500');


ALTER LARGE OBJECT 50500 OWNER TO discs_ccdb;

--
-- TOC entry 2300 (class 2613 OID 50501)
-- Name: 50501; Type: BLOB; Schema: -; Owner: discs_ccdb
--

SELECT pg_catalog.lo_create('50501');


ALTER LARGE OBJECT 50501 OWNER TO discs_ccdb;

--
-- TOC entry 2301 (class 2613 OID 50502)
-- Name: 50502; Type: BLOB; Schema: -; Owner: discs_ccdb
--

SELECT pg_catalog.lo_create('50502');


ALTER LARGE OBJECT 50502 OWNER TO discs_ccdb;

--
-- TOC entry 2302 (class 2613 OID 50503)
-- Name: 50503; Type: BLOB; Schema: -; Owner: discs_ccdb
--

SELECT pg_catalog.lo_create('50503');


ALTER LARGE OBJECT 50503 OWNER TO discs_ccdb;

--
-- TOC entry 2303 (class 2613 OID 50504)
-- Name: 50504; Type: BLOB; Schema: -; Owner: discs_ccdb
--

SELECT pg_catalog.lo_create('50504');


ALTER LARGE OBJECT 50504 OWNER TO discs_ccdb;

--
-- TOC entry 2304 (class 2613 OID 50505)
-- Name: 50505; Type: BLOB; Schema: -; Owner: discs_ccdb
--

SELECT pg_catalog.lo_create('50505');


ALTER LARGE OBJECT 50505 OWNER TO discs_ccdb;

--
-- TOC entry 2305 (class 2613 OID 50507)
-- Name: 50507; Type: BLOB; Schema: -; Owner: discs_ccdb
--

SELECT pg_catalog.lo_create('50507');


ALTER LARGE OBJECT 50507 OWNER TO discs_ccdb;

--
-- TOC entry 2306 (class 2613 OID 50508)
-- Name: 50508; Type: BLOB; Schema: -; Owner: discs_ccdb
--

SELECT pg_catalog.lo_create('50508');


ALTER LARGE OBJECT 50508 OWNER TO discs_ccdb;

--
-- TOC entry 2307 (class 2613 OID 50510)
-- Name: 50510; Type: BLOB; Schema: -; Owner: discs_ccdb
--

SELECT pg_catalog.lo_create('50510');


ALTER LARGE OBJECT 50510 OWNER TO discs_ccdb;

--
-- TOC entry 2243 (class 0 OID 41855)
-- Dependencies: 172
-- Data for Name: alignment_artifact; Type: TABLE DATA; Schema: public; Owner: discs_ccdb
--

COPY alignment_artifact (artifact_id, description, is_internal, modified_at, modified_by, name, uri, alignment_record) FROM stdin;
\.


--
-- TOC entry 2385 (class 0 OID 0)
-- Dependencies: 171
-- Name: alignment_artifact_artifact_id_seq; Type: SEQUENCE SET; Schema: public; Owner: discs_ccdb
--

SELECT pg_catalog.setval('alignment_artifact_artifact_id_seq', 1, false);


--
-- TOC entry 2245 (class 0 OID 41866)
-- Dependencies: 174
-- Data for Name: alignment_property; Type: TABLE DATA; Schema: public; Owner: discs_ccdb
--

COPY alignment_property (align_prop_id, in_repository, modified_at, modified_by, prop_value, version, alignment_record, property) FROM stdin;
\.


--
-- TOC entry 2386 (class 0 OID 0)
-- Dependencies: 173
-- Name: alignment_property_align_prop_id_seq; Type: SEQUENCE SET; Schema: public; Owner: discs_ccdb
--

SELECT pg_catalog.setval('alignment_property_align_prop_id_seq', 1, false);


--
-- TOC entry 2247 (class 0 OID 41877)
-- Dependencies: 176
-- Data for Name: alignment_record; Type: TABLE DATA; Schema: public; Owner: discs_ccdb
--

COPY alignment_record (alignment_record_id, alignment_date, global_pitch, global_roll, global_x, global_y, global_yaw, global_z, modified_at, modified_by, record_number, version, device, slot) FROM stdin;
\.


--
-- TOC entry 2387 (class 0 OID 0)
-- Dependencies: 175
-- Name: alignment_record_alignment_record_id_seq; Type: SEQUENCE SET; Schema: public; Owner: discs_ccdb
--

SELECT pg_catalog.setval('alignment_record_alignment_record_id_seq', 1, true);


--
-- TOC entry 2249 (class 0 OID 41888)
-- Dependencies: 178
-- Data for Name: audit_record; Type: TABLE DATA; Schema: public; Owner: discs_ccdb
--

COPY audit_record (audit_record_id, entity_key, entity_type, entry, log_time, oper, "user") FROM stdin;
\.


--
-- TOC entry 2388 (class 0 OID 0)
-- Dependencies: 177
-- Name: audit_record_audit_record_id_seq; Type: SEQUENCE SET; Schema: public; Owner: discs_ccdb
--

SELECT pg_catalog.setval('audit_record_audit_record_id_seq', 1, false);


--
-- TOC entry 2251 (class 0 OID 41899)
-- Dependencies: 180
-- Data for Name: component_type; Type: TABLE DATA; Schema: public; Owner: discs_ccdb
--

COPY component_type (component_type_id, description, modified_at, modified_by, name, version, super_component_type) FROM stdin;
\.


--
-- TOC entry 2389 (class 0 OID 0)
-- Dependencies: 179
-- Name: component_type_component_type_id_seq; Type: SEQUENCE SET; Schema: public; Owner: discs_ccdb
--

SELECT pg_catalog.setval('component_type_component_type_id_seq', 1, false);


--
-- TOC entry 2253 (class 0 OID 41910)
-- Dependencies: 182
-- Data for Name: comptype_artifact; Type: TABLE DATA; Schema: public; Owner: discs_ccdb
--

COPY comptype_artifact (artifact_id, description, is_internal, modified_at, modified_by, name, uri, component_type) FROM stdin;
\.


--
-- TOC entry 2390 (class 0 OID 0)
-- Dependencies: 181
-- Name: comptype_artifact_artifact_id_seq; Type: SEQUENCE SET; Schema: public; Owner: discs_ccdb
--

SELECT pg_catalog.setval('comptype_artifact_artifact_id_seq', 1, false);


--
-- TOC entry 2255 (class 0 OID 41921)
-- Dependencies: 184
-- Data for Name: comptype_asm; Type: TABLE DATA; Schema: public; Owner: discs_ccdb
--

COPY comptype_asm (comptype_asm_id, child_position, description, modified_at, modified_by, version, child_type, parent_type) FROM stdin;
\.


--
-- TOC entry 2391 (class 0 OID 0)
-- Dependencies: 183
-- Name: comptype_asm_comptype_asm_id_seq; Type: SEQUENCE SET; Schema: public; Owner: discs_ccdb
--

SELECT pg_catalog.setval('comptype_asm_comptype_asm_id_seq', 1, false);


--
-- TOC entry 2257 (class 0 OID 41932)
-- Dependencies: 186
-- Data for Name: comptype_property; Type: TABLE DATA; Schema: public; Owner: discs_ccdb
--

COPY comptype_property (ctype_prop_id, in_repository, modified_at, modified_by, prop_value, type, version, component_type, property, unit) FROM stdin;
\.


--
-- TOC entry 2392 (class 0 OID 0)
-- Dependencies: 185
-- Name: comptype_property_ctype_prop_id_seq; Type: SEQUENCE SET; Schema: public; Owner: discs_ccdb
--

SELECT pg_catalog.setval('comptype_property_ctype_prop_id_seq', 1, false);


--
-- TOC entry 2258 (class 0 OID 41941)
-- Dependencies: 187
-- Data for Name: config; Type: TABLE DATA; Schema: public; Owner: discs_ccdb
--

COPY config (name, prop_value) FROM stdin;
\.


--
-- TOC entry 2259 (class 0 OID 41949)
-- Dependencies: 188
-- Data for Name: data_type; Type: TABLE DATA; Schema: public; Owner: discs_ccdb
--

COPY data_type (data_type_id, description, modified_at, modified_by, scalar, version) FROM stdin;
\.


--
-- TOC entry 2261 (class 0 OID 41959)
-- Dependencies: 190
-- Data for Name: device; Type: TABLE DATA; Schema: public; Owner: discs_ccdb
--

COPY device (device_id, asm_description, asm_position, description, location, manuf_model, manuf_serial_number, manufacturer, modified_at, modified_by, purchase_order, serial_number, status, version, asm_parent, component_type, uuid) FROM stdin;
\.


--
-- TOC entry 2263 (class 0 OID 41970)
-- Dependencies: 192
-- Data for Name: device_artifact; Type: TABLE DATA; Schema: public; Owner: discs_ccdb
--

COPY device_artifact (artifact_id, description, is_internal, modified_at, modified_by, name, uri, device) FROM stdin;
\.


--
-- TOC entry 2393 (class 0 OID 0)
-- Dependencies: 191
-- Name: device_artifact_artifact_id_seq; Type: SEQUENCE SET; Schema: public; Owner: discs_ccdb
--

SELECT pg_catalog.setval('device_artifact_artifact_id_seq', 1, false);


--
-- TOC entry 2394 (class 0 OID 0)
-- Dependencies: 189
-- Name: device_device_id_seq; Type: SEQUENCE SET; Schema: public; Owner: discs_ccdb
--

SELECT pg_catalog.setval('device_device_id_seq', 1, false);


--
-- TOC entry 2265 (class 0 OID 41981)
-- Dependencies: 194
-- Data for Name: device_property; Type: TABLE DATA; Schema: public; Owner: discs_ccdb
--

COPY device_property (dev_prop_id, in_repository, modified_at, modified_by, prop_value, version, device, property, unit) FROM stdin;
\.


--
-- TOC entry 2395 (class 0 OID 0)
-- Dependencies: 193
-- Name: device_property_dev_prop_id_seq; Type: SEQUENCE SET; Schema: public; Owner: discs_ccdb
--

SELECT pg_catalog.setval('device_property_dev_prop_id_seq', 1, false);


--
-- TOC entry 2267 (class 0 OID 41992)
-- Dependencies: 196
-- Data for Name: installation_artifact; Type: TABLE DATA; Schema: public; Owner: discs_ccdb
--

COPY installation_artifact (artifact_id, description, is_internal, modified_at, modified_by, name, uri, installation_record) FROM stdin;
\.


--
-- TOC entry 2396 (class 0 OID 0)
-- Dependencies: 195
-- Name: installation_artifact_artifact_id_seq; Type: SEQUENCE SET; Schema: public; Owner: discs_ccdb
--

SELECT pg_catalog.setval('installation_artifact_artifact_id_seq', 1, false);


--
-- TOC entry 2269 (class 0 OID 42003)
-- Dependencies: 198
-- Data for Name: installation_record; Type: TABLE DATA; Schema: public; Owner: discs_ccdb
--

COPY installation_record (installation_record_id, install_date, modified_at, modified_by, notes, record_number, uninstall_date, version, device, slot, uuid) FROM stdin;
\.


--
-- TOC entry 2397 (class 0 OID 0)
-- Dependencies: 197
-- Name: installation_record_installation_record_id_seq; Type: SEQUENCE SET; Schema: public; Owner: discs_ccdb
--

SELECT pg_catalog.setval('installation_record_installation_record_id_seq', 1, false);


--
-- TOC entry 2271 (class 0 OID 42014)
-- Dependencies: 200
-- Data for Name: privilege; Type: TABLE DATA; Schema: public; Owner: discs_ccdb
--

COPY privilege (privilege_id, oper, resource, role) FROM stdin;
21  UPDATE  INSTALLATION_RECORD admin
31  AUTHORIZED  ALIGNMENT_RECORD    admin
30  LOGOUT  ALIGNMENT_RECORD    admin
29  LOGIN   ALIGNMENT_RECORD    admin
28  DELETE  ALIGNMENT_RECORD    admin
27  CREATE  ALIGNMENT_RECORD    admin
26  UPDATE  ALIGNMENT_RECORD    admin
32  AUTHORIZED  DEVICE  admin
33  AUTHORIZED  SLOT    admin
34  AUTHORIZED  COMPONENT_TYPE  admin
35  AUTHORIZED  USER    admin
36  AUTHORIZED  INSTALLATION_RECORD admin
37  AUTHORIZED  MENU    admin
1   UPDATE  DEVICE  admin
5   LOGOUT  DEVICE  admin
4   LOGIN   DEVICE  admin
3   DELETE  DEVICE  admin
2   CREATE  DEVICE  admin
10  LOGOUT  SLOT    admin
9   LOGIN   SLOT    admin
8   DELETE  SLOT    admin
7   CREATE  SLOT    admin
6   UPDATE  SLOT    admin
15  LOGOUT  COMPONENT_TYPE  admin
14  LOGIN   COMPONENT_TYPE  admin
13  DELETE  COMPONENT_TYPE  admin
12  CREATE  COMPONENT_TYPE  admin
11  UPDATE  COMPONENT_TYPE  admin
20  LOGOUT  USER    admin
19  LOGIN   USER    admin
18  DELETE  USER    admin
17  CREATE  USER    admin
16  UPDATE  USER    admin
25  LOGOUT  INSTALLATION_RECORD admin
24  LOGIN   INSTALLATION_RECORD admin
23  DELETE  INSTALLATION_RECORD admin
22  CREATE  INSTALLATION_RECORD admin
\.


--
-- TOC entry 2398 (class 0 OID 0)
-- Dependencies: 199
-- Name: privilege_privilege_id_seq; Type: SEQUENCE SET; Schema: public; Owner: discs_ccdb
--

SELECT pg_catalog.setval('privilege_privilege_id_seq', 1, false);


--
-- TOC entry 2273 (class 0 OID 42025)
-- Dependencies: 202
-- Data for Name: property; Type: TABLE DATA; Schema: public; Owner: discs_ccdb
--

COPY property (property_id, association, description, modified_at, modified_by, name, version, data_type, unit) FROM stdin;
\.


--
-- TOC entry 2399 (class 0 OID 0)
-- Dependencies: 201
-- Name: property_property_id_seq; Type: SEQUENCE SET; Schema: public; Owner: discs_ccdb
--

SELECT pg_catalog.setval('property_property_id_seq', 1, false);


--
-- TOC entry 2274 (class 0 OID 42034)
-- Dependencies: 203
-- Data for Name: role; Type: TABLE DATA; Schema: public; Owner: discs_ccdb
--

COPY role (role_id, description, version) FROM stdin;
admin   admin   0
\.


--
-- TOC entry 2276 (class 0 OID 42044)
-- Dependencies: 205
-- Data for Name: slot; Type: TABLE DATA; Schema: public; Owner: discs_ccdb
--

COPY slot (slot_id, asm_comment, asm_position, beamline_position, comment, description, global_pitch, global_roll, global_x, global_y, global_yaw, global_z, is_hosting_slot, modified_at, modified_by, name, version, asm_slot, component_type, uuid) FROM stdin;
\.


--
-- TOC entry 2278 (class 0 OID 42055)
-- Dependencies: 207
-- Data for Name: slot_artifact; Type: TABLE DATA; Schema: public; Owner: discs_ccdb
--

COPY slot_artifact (artifact_id, description, is_internal, modified_at, modified_by, name, uri, slot) FROM stdin;
\.


--
-- TOC entry 2400 (class 0 OID 0)
-- Dependencies: 206
-- Name: slot_artifact_artifact_id_seq; Type: SEQUENCE SET; Schema: public; Owner: discs_ccdb
--

SELECT pg_catalog.setval('slot_artifact_artifact_id_seq', 1, false);


--
-- TOC entry 2280 (class 0 OID 42066)
-- Dependencies: 209
-- Data for Name: slot_pair; Type: TABLE DATA; Schema: public; Owner: discs_ccdb
--

COPY slot_pair (slot_pair_id, version, child_slot, parent_slot, slot_relation) FROM stdin;
\.


--
-- TOC entry 2401 (class 0 OID 0)
-- Dependencies: 208
-- Name: slot_pair_slot_pair_id_seq; Type: SEQUENCE SET; Schema: public; Owner: discs_ccdb
--

SELECT pg_catalog.setval('slot_pair_slot_pair_id_seq', 1, false);


--
-- TOC entry 2282 (class 0 OID 42074)
-- Dependencies: 211
-- Data for Name: slot_property; Type: TABLE DATA; Schema: public; Owner: discs_ccdb
--

COPY slot_property (slot_prop_id, in_repository, modified_at, modified_by, prop_value, version, property, slot, unit) FROM stdin;
\.


--
-- TOC entry 2402 (class 0 OID 0)
-- Dependencies: 210
-- Name: slot_property_slot_prop_id_seq; Type: SEQUENCE SET; Schema: public; Owner: discs_ccdb
--

SELECT pg_catalog.setval('slot_property_slot_prop_id_seq', 1, false);


--
-- TOC entry 2284 (class 0 OID 42085)
-- Dependencies: 213
-- Data for Name: slot_relation; Type: TABLE DATA; Schema: public; Owner: discs_ccdb
--

COPY slot_relation (slot_relation_id, description, iname, modified_at, modified_by, name, version) FROM stdin;
\.


--
-- TOC entry 2403 (class 0 OID 0)
-- Dependencies: 212
-- Name: slot_relation_slot_relation_id_seq; Type: SEQUENCE SET; Schema: public; Owner: discs_ccdb
--

SELECT pg_catalog.setval('slot_relation_slot_relation_id_seq', 1, false);


--
-- TOC entry 2404 (class 0 OID 0)
-- Dependencies: 204
-- Name: slot_slot_id_seq; Type: SEQUENCE SET; Schema: public; Owner: discs_ccdb
--

SELECT pg_catalog.setval('slot_slot_id_seq', 3, true);


--
-- TOC entry 2285 (class 0 OID 42094)
-- Dependencies: 214
-- Data for Name: unit; Type: TABLE DATA; Schema: public; Owner: discs_ccdb
--

COPY unit (unit_id, base_unit_expr, description, modified_at, modified_by, quantity, symbol, version) FROM stdin;
\.


--
-- TOC entry 2241 (class 0 OID 41845)
-- Dependencies: 170
-- Data for Name: user; Type: TABLE DATA; Schema: public; Owner: discs_ccdb
--

COPY "user" (user_id, comment, email, name, version) FROM stdin;
admin   admin   admin@admin.com admin   0
\.


--
-- TOC entry 2287 (class 0 OID 42104)
-- Dependencies: 216
-- Data for Name: user_role; Type: TABLE DATA; Schema: public; Owner: discs_ccdb
--

COPY user_role (user_role_id, candelegate, comment, endtime, isrolemanager, starttime, version, role, "user") FROM stdin;
1   t   admin   2050-01-08 04:05:06 t   2001-01-08 04:05:06 0   admin   admin
\.


--
-- TOC entry 2405 (class 0 OID 0)
-- Dependencies: 215
-- Name: user_role_user_role_id_seq; Type: SEQUENCE SET; Schema: public; Owner: discs_ccdb
--

SELECT pg_catalog.setval('user_role_user_role_id_seq', 1, false);


--
-- TOC entry 2308 (class 0 OID 0)
-- Data for Name: BLOBS; Type: BLOBS; Schema: -; Owner: 
--

SET search_path = pg_catalog;

BEGIN;

SELECT pg_catalog.lo_open('42289', 131072);
SELECT pg_catalog.lowrite(0, '\x5570646174656420636f6d706f6e656e742074797065');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('42290', 131072);
SELECT pg_catalog.lowrite(0, '\x4d6f64696669656420736c6f74');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('42291', 131072);
SELECT pg_catalog.lowrite(0, '\x4d6f64696669656420646576696365');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('42299', 131072);
SELECT pg_catalog.lowrite(0, '\x617364');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('42300', 131072);
SELECT pg_catalog.lowrite(0, '\x7570646174656420696e7374616c6c6174696f6e207265636f726420');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('42302', 131072);
SELECT pg_catalog.lowrite(0, '\x717765');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('42303', 131072);
SELECT pg_catalog.lowrite(0, '\x7570646174656420696e7374616c6c6174696f6e207265636f726420');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('42307', 131072);
SELECT pg_catalog.lowrite(0, '\x44656c6574656420736c6f74');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('42308', 131072);
SELECT pg_catalog.lowrite(0, '\x4d6f64696669656420736c6f74');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('50498', 131072);
SELECT pg_catalog.lowrite(0, '\x44656c6574656420646576696365');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('50499', 131072);
SELECT pg_catalog.lowrite(0, '\x4d6f64696669656420646576696365');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('50500', 131072);
SELECT pg_catalog.lowrite(0, '\x44656c6574656420736c6f74');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('50501', 131072);
SELECT pg_catalog.lowrite(0, '\x4d6f64696669656420736c6f74');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('50502', 131072);
SELECT pg_catalog.lowrite(0, '\x4d6f64696669656420646576696365');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('50503', 131072);
SELECT pg_catalog.lowrite(0, '\x44656c6574656420646576696365');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('50504', 131072);
SELECT pg_catalog.lowrite(0, '\x44656c6574656420646576696365');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('50505', 131072);
SELECT pg_catalog.lowrite(0, '\x4d6f64696669656420646576696365');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('50507', 131072);
SELECT pg_catalog.lowrite(0, '\x617364');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('50508', 131072);
SELECT pg_catalog.lowrite(0, '\x7570646174656420696e7374616c6c6174696f6e207265636f726420');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('50510', 131072);
SELECT pg_catalog.lowrite(0, '\x7570646174656420616c69676e6d656e74207265636f7264');
SELECT pg_catalog.lo_close(0);

COMMIT;

SET search_path = public, pg_catalog;

--
-- TOC entry 2050 (class 2606 OID 41863)
-- Name: alignment_artifact_pkey; Type: CONSTRAINT; Schema: public; Owner: discs_ccdb; Tablespace: 
--

ALTER TABLE ONLY alignment_artifact
    ADD CONSTRAINT alignment_artifact_pkey PRIMARY KEY (artifact_id);


--
-- TOC entry 2052 (class 2606 OID 41874)
-- Name: alignment_property_pkey; Type: CONSTRAINT; Schema: public; Owner: discs_ccdb; Tablespace: 
--

ALTER TABLE ONLY alignment_property
    ADD CONSTRAINT alignment_property_pkey PRIMARY KEY (align_prop_id);


--
-- TOC entry 2054 (class 2606 OID 41885)
-- Name: alignment_record_pkey; Type: CONSTRAINT; Schema: public; Owner: discs_ccdb; Tablespace: 
--

ALTER TABLE ONLY alignment_record
    ADD CONSTRAINT alignment_record_pkey PRIMARY KEY (alignment_record_id);


--
-- TOC entry 2056 (class 2606 OID 41896)
-- Name: audit_record_pkey; Type: CONSTRAINT; Schema: public; Owner: discs_ccdb; Tablespace: 
--

ALTER TABLE ONLY audit_record
    ADD CONSTRAINT audit_record_pkey PRIMARY KEY (audit_record_id);


--
-- TOC entry 2058 (class 2606 OID 41907)
-- Name: component_type_pkey; Type: CONSTRAINT; Schema: public; Owner: discs_ccdb; Tablespace: 
--

ALTER TABLE ONLY component_type
    ADD CONSTRAINT component_type_pkey PRIMARY KEY (component_type_id);


--
-- TOC entry 2060 (class 2606 OID 41918)
-- Name: comptype_artifact_pkey; Type: CONSTRAINT; Schema: public; Owner: discs_ccdb; Tablespace: 
--

ALTER TABLE ONLY comptype_artifact
    ADD CONSTRAINT comptype_artifact_pkey PRIMARY KEY (artifact_id);


--
-- TOC entry 2062 (class 2606 OID 41929)
-- Name: comptype_asm_pkey; Type: CONSTRAINT; Schema: public; Owner: discs_ccdb; Tablespace: 
--

ALTER TABLE ONLY comptype_asm
    ADD CONSTRAINT comptype_asm_pkey PRIMARY KEY (comptype_asm_id);


--
-- TOC entry 2064 (class 2606 OID 41940)
-- Name: comptype_property_pkey; Type: CONSTRAINT; Schema: public; Owner: discs_ccdb; Tablespace: 
--

ALTER TABLE ONLY comptype_property
    ADD CONSTRAINT comptype_property_pkey PRIMARY KEY (ctype_prop_id);


--
-- TOC entry 2066 (class 2606 OID 41948)
-- Name: config_pkey; Type: CONSTRAINT; Schema: public; Owner: discs_ccdb; Tablespace: 
--

ALTER TABLE ONLY config
    ADD CONSTRAINT config_pkey PRIMARY KEY (name);


--
-- TOC entry 2068 (class 2606 OID 41956)
-- Name: data_type_pkey; Type: CONSTRAINT; Schema: public; Owner: discs_ccdb; Tablespace: 
--

ALTER TABLE ONLY data_type
    ADD CONSTRAINT data_type_pkey PRIMARY KEY (data_type_id);


--
-- TOC entry 2072 (class 2606 OID 41978)
-- Name: device_artifact_pkey; Type: CONSTRAINT; Schema: public; Owner: discs_ccdb; Tablespace: 
--

ALTER TABLE ONLY device_artifact
    ADD CONSTRAINT device_artifact_pkey PRIMARY KEY (artifact_id);


--
-- TOC entry 2070 (class 2606 OID 41967)
-- Name: device_pkey; Type: CONSTRAINT; Schema: public; Owner: discs_ccdb; Tablespace: 
--

ALTER TABLE ONLY device
    ADD CONSTRAINT device_pkey PRIMARY KEY (device_id);


--
-- TOC entry 2074 (class 2606 OID 41989)
-- Name: device_property_pkey; Type: CONSTRAINT; Schema: public; Owner: discs_ccdb; Tablespace: 
--

ALTER TABLE ONLY device_property
    ADD CONSTRAINT device_property_pkey PRIMARY KEY (dev_prop_id);


--
-- TOC entry 2076 (class 2606 OID 42000)
-- Name: installation_artifact_pkey; Type: CONSTRAINT; Schema: public; Owner: discs_ccdb; Tablespace: 
--

ALTER TABLE ONLY installation_artifact
    ADD CONSTRAINT installation_artifact_pkey PRIMARY KEY (artifact_id);


--
-- TOC entry 2078 (class 2606 OID 42011)
-- Name: installation_record_pkey; Type: CONSTRAINT; Schema: public; Owner: discs_ccdb; Tablespace: 
--

ALTER TABLE ONLY installation_record
    ADD CONSTRAINT installation_record_pkey PRIMARY KEY (installation_record_id);


--
-- TOC entry 2080 (class 2606 OID 42022)
-- Name: privilege_pkey; Type: CONSTRAINT; Schema: public; Owner: discs_ccdb; Tablespace: 
--

ALTER TABLE ONLY privilege
    ADD CONSTRAINT privilege_pkey PRIMARY KEY (privilege_id);


--
-- TOC entry 2082 (class 2606 OID 42033)
-- Name: property_pkey; Type: CONSTRAINT; Schema: public; Owner: discs_ccdb; Tablespace: 
--

ALTER TABLE ONLY property
    ADD CONSTRAINT property_pkey PRIMARY KEY (property_id);


--
-- TOC entry 2084 (class 2606 OID 42041)
-- Name: role_pkey; Type: CONSTRAINT; Schema: public; Owner: discs_ccdb; Tablespace: 
--

ALTER TABLE ONLY role
    ADD CONSTRAINT role_pkey PRIMARY KEY (role_id);


--
-- TOC entry 2088 (class 2606 OID 42063)
-- Name: slot_artifact_pkey; Type: CONSTRAINT; Schema: public; Owner: discs_ccdb; Tablespace: 
--

ALTER TABLE ONLY slot_artifact
    ADD CONSTRAINT slot_artifact_pkey PRIMARY KEY (artifact_id);


--
-- TOC entry 2090 (class 2606 OID 42071)
-- Name: slot_pair_pkey; Type: CONSTRAINT; Schema: public; Owner: discs_ccdb; Tablespace: 
--

ALTER TABLE ONLY slot_pair
    ADD CONSTRAINT slot_pair_pkey PRIMARY KEY (slot_pair_id);


--
-- TOC entry 2086 (class 2606 OID 42052)
-- Name: slot_pkey; Type: CONSTRAINT; Schema: public; Owner: discs_ccdb; Tablespace: 
--

ALTER TABLE ONLY slot
    ADD CONSTRAINT slot_pkey PRIMARY KEY (slot_id);


--
-- TOC entry 2092 (class 2606 OID 42082)
-- Name: slot_property_pkey; Type: CONSTRAINT; Schema: public; Owner: discs_ccdb; Tablespace: 
--

ALTER TABLE ONLY slot_property
    ADD CONSTRAINT slot_property_pkey PRIMARY KEY (slot_prop_id);


--
-- TOC entry 2094 (class 2606 OID 42093)
-- Name: slot_relation_pkey; Type: CONSTRAINT; Schema: public; Owner: discs_ccdb; Tablespace: 
--

ALTER TABLE ONLY slot_relation
    ADD CONSTRAINT slot_relation_pkey PRIMARY KEY (slot_relation_id);


--
-- TOC entry 2096 (class 2606 OID 42101)
-- Name: unit_pkey; Type: CONSTRAINT; Schema: public; Owner: discs_ccdb; Tablespace: 
--

ALTER TABLE ONLY unit
    ADD CONSTRAINT unit_pkey PRIMARY KEY (unit_id);


--
-- TOC entry 2048 (class 2606 OID 41852)
-- Name: user_pkey; Type: CONSTRAINT; Schema: public; Owner: discs_ccdb; Tablespace: 
--

ALTER TABLE ONLY "user"
    ADD CONSTRAINT user_pkey PRIMARY KEY (user_id);


--
-- TOC entry 2098 (class 2606 OID 42112)
-- Name: user_role_pkey; Type: CONSTRAINT; Schema: public; Owner: discs_ccdb; Tablespace: 
--

ALTER TABLE ONLY user_role
    ADD CONSTRAINT user_role_pkey PRIMARY KEY (user_role_id);


--
-- TOC entry 2132 (class 2606 OID 42278)
-- Name: fk143bf46af7692ed5; Type: FK CONSTRAINT; Schema: public; Owner: discs_ccdb
--

ALTER TABLE ONLY user_role
    ADD CONSTRAINT fk143bf46af7692ed5 FOREIGN KEY (role) REFERENCES role(role_id);


--
-- TOC entry 2133 (class 2606 OID 42283)
-- Name: fk143bf46af76c057f; Type: FK CONSTRAINT; Schema: public; Owner: discs_ccdb
--

ALTER TABLE ONLY user_role
    ADD CONSTRAINT fk143bf46af76c057f FOREIGN KEY ("user") REFERENCES "user"(user_id);


--
-- TOC entry 2109 (class 2606 OID 42163)
-- Name: fk18f5828b588ecd13; Type: FK CONSTRAINT; Schema: public; Owner: discs_ccdb
--

ALTER TABLE ONLY comptype_property
    ADD CONSTRAINT fk18f5828b588ecd13 FOREIGN KEY (property) REFERENCES property(property_id);


--
-- TOC entry 2108 (class 2606 OID 42158)
-- Name: fk18f5828bf76be0f1; Type: FK CONSTRAINT; Schema: public; Owner: discs_ccdb
--

ALTER TABLE ONLY comptype_property
    ADD CONSTRAINT fk18f5828bf76be0f1 FOREIGN KEY (unit) REFERENCES unit(unit_id);


--
-- TOC entry 2110 (class 2606 OID 42168)
-- Name: fk18f5828bfde0834a; Type: FK CONSTRAINT; Schema: public; Owner: discs_ccdb
--

ALTER TABLE ONLY comptype_property
    ADD CONSTRAINT fk18f5828bfde0834a FOREIGN KEY (component_type) REFERENCES component_type(component_type_id);


--
-- TOC entry 2119 (class 2606 OID 42213)
-- Name: fk232775f691009455; Type: FK CONSTRAINT; Schema: public; Owner: discs_ccdb
--

ALTER TABLE ONLY installation_record
    ADD CONSTRAINT fk232775f691009455 FOREIGN KEY (device) REFERENCES device(device_id);


--
-- TOC entry 2118 (class 2606 OID 42208)
-- Name: fk232775f6f76a01e5; Type: FK CONSTRAINT; Schema: public; Owner: discs_ccdb
--

ALTER TABLE ONLY installation_record
    ADD CONSTRAINT fk232775f6f76a01e5 FOREIGN KEY (slot) REFERENCES slot(slot_id);


--
-- TOC entry 2126 (class 2606 OID 42248)
-- Name: fk28f1c07b33b68c5a; Type: FK CONSTRAINT; Schema: public; Owner: discs_ccdb
--

ALTER TABLE ONLY slot_pair
    ADD CONSTRAINT fk28f1c07b33b68c5a FOREIGN KEY (parent_slot) REFERENCES slot(slot_id);


--
-- TOC entry 2127 (class 2606 OID 42253)
-- Name: fk28f1c07b47f99728; Type: FK CONSTRAINT; Schema: public; Owner: discs_ccdb
--

ALTER TABLE ONLY slot_pair
    ADD CONSTRAINT fk28f1c07b47f99728 FOREIGN KEY (child_slot) REFERENCES slot(slot_id);


--
-- TOC entry 2128 (class 2606 OID 42258)
-- Name: fk28f1c07bde533e20; Type: FK CONSTRAINT; Schema: public; Owner: discs_ccdb
--

ALTER TABLE ONLY slot_pair
    ADD CONSTRAINT fk28f1c07bde533e20 FOREIGN KEY (slot_relation) REFERENCES slot_relation(slot_relation_id);


--
-- TOC entry 2123 (class 2606 OID 42233)
-- Name: fk35e9fed6639529; Type: FK CONSTRAINT; Schema: public; Owner: discs_ccdb
--

ALTER TABLE ONLY slot
    ADD CONSTRAINT fk35e9fed6639529 FOREIGN KEY (asm_slot) REFERENCES slot(slot_id);


--
-- TOC entry 2124 (class 2606 OID 42238)
-- Name: fk35e9fefde0834a; Type: FK CONSTRAINT; Schema: public; Owner: discs_ccdb
--

ALTER TABLE ONLY slot
    ADD CONSTRAINT fk35e9fefde0834a FOREIGN KEY (component_type) REFERENCES component_type(component_type_id);


--
-- TOC entry 2099 (class 2606 OID 42113)
-- Name: fk3aec04aec5926cb8; Type: FK CONSTRAINT; Schema: public; Owner: discs_ccdb
--

ALTER TABLE ONLY alignment_artifact
    ADD CONSTRAINT fk3aec04aec5926cb8 FOREIGN KEY (alignment_record) REFERENCES alignment_record(alignment_record_id);


--
-- TOC entry 2103 (class 2606 OID 42133)
-- Name: fk415a722d91009455; Type: FK CONSTRAINT; Schema: public; Owner: discs_ccdb
--

ALTER TABLE ONLY alignment_record
    ADD CONSTRAINT fk415a722d91009455 FOREIGN KEY (device) REFERENCES device(device_id);


--
-- TOC entry 2102 (class 2606 OID 42128)
-- Name: fk415a722df76a01e5; Type: FK CONSTRAINT; Schema: public; Owner: discs_ccdb
--

ALTER TABLE ONLY alignment_record
    ADD CONSTRAINT fk415a722df76a01e5 FOREIGN KEY (slot) REFERENCES slot(slot_id);


--
-- TOC entry 2106 (class 2606 OID 42148)
-- Name: fk41c73c05e8b5dbbd; Type: FK CONSTRAINT; Schema: public; Owner: discs_ccdb
--

ALTER TABLE ONLY comptype_asm
    ADD CONSTRAINT fk41c73c05e8b5dbbd FOREIGN KEY (parent_type) REFERENCES component_type(component_type_id);


--
-- TOC entry 2107 (class 2606 OID 42153)
-- Name: fk41c73c05fcf8e68b; Type: FK CONSTRAINT; Schema: public; Owner: discs_ccdb
--

ALTER TABLE ONLY comptype_asm
    ADD CONSTRAINT fk41c73c05fcf8e68b FOREIGN KEY (child_type) REFERENCES component_type(component_type_id);


--
-- TOC entry 2101 (class 2606 OID 42123)
-- Name: fk48f7dcb1588ecd13; Type: FK CONSTRAINT; Schema: public; Owner: discs_ccdb
--

ALTER TABLE ONLY alignment_property
    ADD CONSTRAINT fk48f7dcb1588ecd13 FOREIGN KEY (property) REFERENCES property(property_id);


--
-- TOC entry 2100 (class 2606 OID 42118)
-- Name: fk48f7dcb1c5926cb8; Type: FK CONSTRAINT; Schema: public; Owner: discs_ccdb
--

ALTER TABLE ONLY alignment_property
    ADD CONSTRAINT fk48f7dcb1c5926cb8 FOREIGN KEY (alignment_record) REFERENCES alignment_record(alignment_record_id);


--
-- TOC entry 2104 (class 2606 OID 42138)
-- Name: fk51adc13ca8ab3c8e; Type: FK CONSTRAINT; Schema: public; Owner: discs_ccdb
--

ALTER TABLE ONLY component_type
    ADD CONSTRAINT fk51adc13ca8ab3c8e FOREIGN KEY (super_component_type) REFERENCES component_type(component_type_id);


--
-- TOC entry 2125 (class 2606 OID 42243)
-- Name: fk6065aaf3f76a01e5; Type: FK CONSTRAINT; Schema: public; Owner: discs_ccdb
--

ALTER TABLE ONLY slot_artifact
    ADD CONSTRAINT fk6065aaf3f76a01e5 FOREIGN KEY (slot) REFERENCES slot(slot_id);


--
-- TOC entry 2131 (class 2606 OID 42273)
-- Name: fk6e7182f6588ecd13; Type: FK CONSTRAINT; Schema: public; Owner: discs_ccdb
--

ALTER TABLE ONLY slot_property
    ADD CONSTRAINT fk6e7182f6588ecd13 FOREIGN KEY (property) REFERENCES property(property_id);


--
-- TOC entry 2130 (class 2606 OID 42268)
-- Name: fk6e7182f6f76a01e5; Type: FK CONSTRAINT; Schema: public; Owner: discs_ccdb
--

ALTER TABLE ONLY slot_property
    ADD CONSTRAINT fk6e7182f6f76a01e5 FOREIGN KEY (slot) REFERENCES slot(slot_id);


--
-- TOC entry 2129 (class 2606 OID 42263)
-- Name: fk6e7182f6f76be0f1; Type: FK CONSTRAINT; Schema: public; Owner: discs_ccdb
--

ALTER TABLE ONLY slot_property
    ADD CONSTRAINT fk6e7182f6f76be0f1 FOREIGN KEY (unit) REFERENCES unit(unit_id);


--
-- TOC entry 2113 (class 2606 OID 42183)
-- Name: fk9d99b91009455; Type: FK CONSTRAINT; Schema: public; Owner: discs_ccdb
--

ALTER TABLE ONLY device_artifact
    ADD CONSTRAINT fk9d99b91009455 FOREIGN KEY (device) REFERENCES device(device_id);


--
-- TOC entry 2120 (class 2606 OID 42218)
-- Name: fka1faf6b1f7692ed5; Type: FK CONSTRAINT; Schema: public; Owner: discs_ccdb
--

ALTER TABLE ONLY privilege
    ADD CONSTRAINT fka1faf6b1f7692ed5 FOREIGN KEY (role) REFERENCES role(role_id);


--
-- TOC entry 2105 (class 2606 OID 42143)
-- Name: fkae9aa88fde0834a; Type: FK CONSTRAINT; Schema: public; Owner: discs_ccdb
--

ALTER TABLE ONLY comptype_artifact
    ADD CONSTRAINT fkae9aa88fde0834a FOREIGN KEY (component_type) REFERENCES component_type(component_type_id);


--
-- TOC entry 2111 (class 2606 OID 42173)
-- Name: fkb06b1e56ac2250ed; Type: FK CONSTRAINT; Schema: public; Owner: discs_ccdb
--

ALTER TABLE ONLY device
    ADD CONSTRAINT fkb06b1e56ac2250ed FOREIGN KEY (asm_parent) REFERENCES device(device_id);


--
-- TOC entry 2112 (class 2606 OID 42178)
-- Name: fkb06b1e56fde0834a; Type: FK CONSTRAINT; Schema: public; Owner: discs_ccdb
--

ALTER TABLE ONLY device
    ADD CONSTRAINT fkb06b1e56fde0834a FOREIGN KEY (component_type) REFERENCES component_type(component_type_id);


--
-- TOC entry 2121 (class 2606 OID 42223)
-- Name: fkc4cdddd523edb21c; Type: FK CONSTRAINT; Schema: public; Owner: discs_ccdb
--

ALTER TABLE ONLY property
    ADD CONSTRAINT fkc4cdddd523edb21c FOREIGN KEY (data_type) REFERENCES data_type(data_type_id);


--
-- TOC entry 2122 (class 2606 OID 42228)
-- Name: fkc4cdddd5f76be0f1; Type: FK CONSTRAINT; Schema: public; Owner: discs_ccdb
--

ALTER TABLE ONLY property
    ADD CONSTRAINT fkc4cdddd5f76be0f1 FOREIGN KEY (unit) REFERENCES unit(unit_id);


--
-- TOC entry 2117 (class 2606 OID 42203)
-- Name: fkdd873a376b4a3b4a; Type: FK CONSTRAINT; Schema: public; Owner: discs_ccdb
--

ALTER TABLE ONLY installation_artifact
    ADD CONSTRAINT fkdd873a376b4a3b4a FOREIGN KEY (installation_record) REFERENCES installation_record(installation_record_id);


--
-- TOC entry 2115 (class 2606 OID 42193)
-- Name: fke15b19e588ecd13; Type: FK CONSTRAINT; Schema: public; Owner: discs_ccdb
--

ALTER TABLE ONLY device_property
    ADD CONSTRAINT fke15b19e588ecd13 FOREIGN KEY (property) REFERENCES property(property_id);


--
-- TOC entry 2116 (class 2606 OID 42198)
-- Name: fke15b19e91009455; Type: FK CONSTRAINT; Schema: public; Owner: discs_ccdb
--

ALTER TABLE ONLY device_property
    ADD CONSTRAINT fke15b19e91009455 FOREIGN KEY (device) REFERENCES device(device_id);


--
-- TOC entry 2114 (class 2606 OID 42188)
-- Name: fke15b19ef76be0f1; Type: FK CONSTRAINT; Schema: public; Owner: discs_ccdb
--

ALTER TABLE ONLY device_property
    ADD CONSTRAINT fke15b19ef76be0f1 FOREIGN KEY (unit) REFERENCES unit(unit_id);


--
-- TOC entry 2315 (class 0 OID 0)
-- Dependencies: 6
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;
GRANT USAGE ON SCHEMA public TO backup_user;


--
-- TOC entry 2317 (class 0 OID 0)
-- Dependencies: 172
-- Name: alignment_artifact; Type: ACL; Schema: public; Owner: discs_ccdb
--

REVOKE ALL ON TABLE alignment_artifact FROM PUBLIC;
REVOKE ALL ON TABLE alignment_artifact FROM discs_ccdb;
GRANT ALL ON TABLE alignment_artifact TO discs_ccdb;
GRANT SELECT ON TABLE alignment_artifact TO backup_user;


--
-- TOC entry 2319 (class 0 OID 0)
-- Dependencies: 171
-- Name: alignment_artifact_artifact_id_seq; Type: ACL; Schema: public; Owner: discs_ccdb
--

REVOKE ALL ON SEQUENCE alignment_artifact_artifact_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE alignment_artifact_artifact_id_seq FROM discs_ccdb;
GRANT ALL ON SEQUENCE alignment_artifact_artifact_id_seq TO discs_ccdb;
GRANT SELECT ON SEQUENCE alignment_artifact_artifact_id_seq TO backup_user;


--
-- TOC entry 2320 (class 0 OID 0)
-- Dependencies: 174
-- Name: alignment_property; Type: ACL; Schema: public; Owner: discs_ccdb
--

REVOKE ALL ON TABLE alignment_property FROM PUBLIC;
REVOKE ALL ON TABLE alignment_property FROM discs_ccdb;
GRANT ALL ON TABLE alignment_property TO discs_ccdb;
GRANT SELECT ON TABLE alignment_property TO backup_user;


--
-- TOC entry 2322 (class 0 OID 0)
-- Dependencies: 173
-- Name: alignment_property_align_prop_id_seq; Type: ACL; Schema: public; Owner: discs_ccdb
--

REVOKE ALL ON SEQUENCE alignment_property_align_prop_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE alignment_property_align_prop_id_seq FROM discs_ccdb;
GRANT ALL ON SEQUENCE alignment_property_align_prop_id_seq TO discs_ccdb;
GRANT SELECT ON SEQUENCE alignment_property_align_prop_id_seq TO backup_user;


--
-- TOC entry 2323 (class 0 OID 0)
-- Dependencies: 176
-- Name: alignment_record; Type: ACL; Schema: public; Owner: discs_ccdb
--

REVOKE ALL ON TABLE alignment_record FROM PUBLIC;
REVOKE ALL ON TABLE alignment_record FROM discs_ccdb;
GRANT ALL ON TABLE alignment_record TO discs_ccdb;
GRANT SELECT ON TABLE alignment_record TO backup_user;


--
-- TOC entry 2325 (class 0 OID 0)
-- Dependencies: 175
-- Name: alignment_record_alignment_record_id_seq; Type: ACL; Schema: public; Owner: discs_ccdb
--

REVOKE ALL ON SEQUENCE alignment_record_alignment_record_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE alignment_record_alignment_record_id_seq FROM discs_ccdb;
GRANT ALL ON SEQUENCE alignment_record_alignment_record_id_seq TO discs_ccdb;
GRANT SELECT ON SEQUENCE alignment_record_alignment_record_id_seq TO backup_user;


--
-- TOC entry 2326 (class 0 OID 0)
-- Dependencies: 178
-- Name: audit_record; Type: ACL; Schema: public; Owner: discs_ccdb
--

REVOKE ALL ON TABLE audit_record FROM PUBLIC;
REVOKE ALL ON TABLE audit_record FROM discs_ccdb;
GRANT ALL ON TABLE audit_record TO discs_ccdb;
GRANT SELECT ON TABLE audit_record TO backup_user;


--
-- TOC entry 2328 (class 0 OID 0)
-- Dependencies: 177
-- Name: audit_record_audit_record_id_seq; Type: ACL; Schema: public; Owner: discs_ccdb
--

REVOKE ALL ON SEQUENCE audit_record_audit_record_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE audit_record_audit_record_id_seq FROM discs_ccdb;
GRANT ALL ON SEQUENCE audit_record_audit_record_id_seq TO discs_ccdb;
GRANT SELECT ON SEQUENCE audit_record_audit_record_id_seq TO backup_user;


--
-- TOC entry 2329 (class 0 OID 0)
-- Dependencies: 180
-- Name: component_type; Type: ACL; Schema: public; Owner: discs_ccdb
--

REVOKE ALL ON TABLE component_type FROM PUBLIC;
REVOKE ALL ON TABLE component_type FROM discs_ccdb;
GRANT ALL ON TABLE component_type TO discs_ccdb;
GRANT SELECT ON TABLE component_type TO backup_user;


--
-- TOC entry 2331 (class 0 OID 0)
-- Dependencies: 179
-- Name: component_type_component_type_id_seq; Type: ACL; Schema: public; Owner: discs_ccdb
--

REVOKE ALL ON SEQUENCE component_type_component_type_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE component_type_component_type_id_seq FROM discs_ccdb;
GRANT ALL ON SEQUENCE component_type_component_type_id_seq TO discs_ccdb;
GRANT SELECT ON SEQUENCE component_type_component_type_id_seq TO backup_user;


--
-- TOC entry 2332 (class 0 OID 0)
-- Dependencies: 182
-- Name: comptype_artifact; Type: ACL; Schema: public; Owner: discs_ccdb
--

REVOKE ALL ON TABLE comptype_artifact FROM PUBLIC;
REVOKE ALL ON TABLE comptype_artifact FROM discs_ccdb;
GRANT ALL ON TABLE comptype_artifact TO discs_ccdb;
GRANT SELECT ON TABLE comptype_artifact TO backup_user;


--
-- TOC entry 2334 (class 0 OID 0)
-- Dependencies: 181
-- Name: comptype_artifact_artifact_id_seq; Type: ACL; Schema: public; Owner: discs_ccdb
--

REVOKE ALL ON SEQUENCE comptype_artifact_artifact_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE comptype_artifact_artifact_id_seq FROM discs_ccdb;
GRANT ALL ON SEQUENCE comptype_artifact_artifact_id_seq TO discs_ccdb;
GRANT SELECT ON SEQUENCE comptype_artifact_artifact_id_seq TO backup_user;


--
-- TOC entry 2335 (class 0 OID 0)
-- Dependencies: 184
-- Name: comptype_asm; Type: ACL; Schema: public; Owner: discs_ccdb
--

REVOKE ALL ON TABLE comptype_asm FROM PUBLIC;
REVOKE ALL ON TABLE comptype_asm FROM discs_ccdb;
GRANT ALL ON TABLE comptype_asm TO discs_ccdb;
GRANT SELECT ON TABLE comptype_asm TO backup_user;


--
-- TOC entry 2337 (class 0 OID 0)
-- Dependencies: 183
-- Name: comptype_asm_comptype_asm_id_seq; Type: ACL; Schema: public; Owner: discs_ccdb
--

REVOKE ALL ON SEQUENCE comptype_asm_comptype_asm_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE comptype_asm_comptype_asm_id_seq FROM discs_ccdb;
GRANT ALL ON SEQUENCE comptype_asm_comptype_asm_id_seq TO discs_ccdb;
GRANT SELECT ON SEQUENCE comptype_asm_comptype_asm_id_seq TO backup_user;


--
-- TOC entry 2338 (class 0 OID 0)
-- Dependencies: 186
-- Name: comptype_property; Type: ACL; Schema: public; Owner: discs_ccdb
--

REVOKE ALL ON TABLE comptype_property FROM PUBLIC;
REVOKE ALL ON TABLE comptype_property FROM discs_ccdb;
GRANT ALL ON TABLE comptype_property TO discs_ccdb;
GRANT SELECT ON TABLE comptype_property TO backup_user;


--
-- TOC entry 2340 (class 0 OID 0)
-- Dependencies: 185
-- Name: comptype_property_ctype_prop_id_seq; Type: ACL; Schema: public; Owner: discs_ccdb
--

REVOKE ALL ON SEQUENCE comptype_property_ctype_prop_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE comptype_property_ctype_prop_id_seq FROM discs_ccdb;
GRANT ALL ON SEQUENCE comptype_property_ctype_prop_id_seq TO discs_ccdb;
GRANT SELECT ON SEQUENCE comptype_property_ctype_prop_id_seq TO backup_user;


--
-- TOC entry 2341 (class 0 OID 0)
-- Dependencies: 187
-- Name: config; Type: ACL; Schema: public; Owner: discs_ccdb
--

REVOKE ALL ON TABLE config FROM PUBLIC;
REVOKE ALL ON TABLE config FROM discs_ccdb;
GRANT ALL ON TABLE config TO discs_ccdb;
GRANT SELECT ON TABLE config TO backup_user;


--
-- TOC entry 2342 (class 0 OID 0)
-- Dependencies: 188
-- Name: data_type; Type: ACL; Schema: public; Owner: discs_ccdb
--

REVOKE ALL ON TABLE data_type FROM PUBLIC;
REVOKE ALL ON TABLE data_type FROM discs_ccdb;
GRANT ALL ON TABLE data_type TO discs_ccdb;
GRANT SELECT ON TABLE data_type TO backup_user;


--
-- TOC entry 2343 (class 0 OID 0)
-- Dependencies: 190
-- Name: device; Type: ACL; Schema: public; Owner: discs_ccdb
--

REVOKE ALL ON TABLE device FROM PUBLIC;
REVOKE ALL ON TABLE device FROM discs_ccdb;
GRANT ALL ON TABLE device TO discs_ccdb;
GRANT SELECT ON TABLE device TO backup_user;


--
-- TOC entry 2344 (class 0 OID 0)
-- Dependencies: 192
-- Name: device_artifact; Type: ACL; Schema: public; Owner: discs_ccdb
--

REVOKE ALL ON TABLE device_artifact FROM PUBLIC;
REVOKE ALL ON TABLE device_artifact FROM discs_ccdb;
GRANT ALL ON TABLE device_artifact TO discs_ccdb;
GRANT SELECT ON TABLE device_artifact TO backup_user;


--
-- TOC entry 2346 (class 0 OID 0)
-- Dependencies: 191
-- Name: device_artifact_artifact_id_seq; Type: ACL; Schema: public; Owner: discs_ccdb
--

REVOKE ALL ON SEQUENCE device_artifact_artifact_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE device_artifact_artifact_id_seq FROM discs_ccdb;
GRANT ALL ON SEQUENCE device_artifact_artifact_id_seq TO discs_ccdb;
GRANT SELECT ON SEQUENCE device_artifact_artifact_id_seq TO backup_user;


--
-- TOC entry 2348 (class 0 OID 0)
-- Dependencies: 189
-- Name: device_device_id_seq; Type: ACL; Schema: public; Owner: discs_ccdb
--

REVOKE ALL ON SEQUENCE device_device_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE device_device_id_seq FROM discs_ccdb;
GRANT ALL ON SEQUENCE device_device_id_seq TO discs_ccdb;
GRANT SELECT ON SEQUENCE device_device_id_seq TO backup_user;


--
-- TOC entry 2349 (class 0 OID 0)
-- Dependencies: 194
-- Name: device_property; Type: ACL; Schema: public; Owner: discs_ccdb
--

REVOKE ALL ON TABLE device_property FROM PUBLIC;
REVOKE ALL ON TABLE device_property FROM discs_ccdb;
GRANT ALL ON TABLE device_property TO discs_ccdb;
GRANT SELECT ON TABLE device_property TO backup_user;


--
-- TOC entry 2351 (class 0 OID 0)
-- Dependencies: 193
-- Name: device_property_dev_prop_id_seq; Type: ACL; Schema: public; Owner: discs_ccdb
--

REVOKE ALL ON SEQUENCE device_property_dev_prop_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE device_property_dev_prop_id_seq FROM discs_ccdb;
GRANT ALL ON SEQUENCE device_property_dev_prop_id_seq TO discs_ccdb;
GRANT SELECT ON SEQUENCE device_property_dev_prop_id_seq TO backup_user;


--
-- TOC entry 2352 (class 0 OID 0)
-- Dependencies: 196
-- Name: installation_artifact; Type: ACL; Schema: public; Owner: discs_ccdb
--

REVOKE ALL ON TABLE installation_artifact FROM PUBLIC;
REVOKE ALL ON TABLE installation_artifact FROM discs_ccdb;
GRANT ALL ON TABLE installation_artifact TO discs_ccdb;
GRANT SELECT ON TABLE installation_artifact TO backup_user;


--
-- TOC entry 2354 (class 0 OID 0)
-- Dependencies: 195
-- Name: installation_artifact_artifact_id_seq; Type: ACL; Schema: public; Owner: discs_ccdb
--

REVOKE ALL ON SEQUENCE installation_artifact_artifact_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE installation_artifact_artifact_id_seq FROM discs_ccdb;
GRANT ALL ON SEQUENCE installation_artifact_artifact_id_seq TO discs_ccdb;
GRANT SELECT ON SEQUENCE installation_artifact_artifact_id_seq TO backup_user;


--
-- TOC entry 2355 (class 0 OID 0)
-- Dependencies: 198
-- Name: installation_record; Type: ACL; Schema: public; Owner: discs_ccdb
--

REVOKE ALL ON TABLE installation_record FROM PUBLIC;
REVOKE ALL ON TABLE installation_record FROM discs_ccdb;
GRANT ALL ON TABLE installation_record TO discs_ccdb;
GRANT SELECT ON TABLE installation_record TO backup_user;


--
-- TOC entry 2357 (class 0 OID 0)
-- Dependencies: 197
-- Name: installation_record_installation_record_id_seq; Type: ACL; Schema: public; Owner: discs_ccdb
--

REVOKE ALL ON SEQUENCE installation_record_installation_record_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE installation_record_installation_record_id_seq FROM discs_ccdb;
GRANT ALL ON SEQUENCE installation_record_installation_record_id_seq TO discs_ccdb;
GRANT SELECT ON SEQUENCE installation_record_installation_record_id_seq TO backup_user;


--
-- TOC entry 2358 (class 0 OID 0)
-- Dependencies: 200
-- Name: privilege; Type: ACL; Schema: public; Owner: discs_ccdb
--

REVOKE ALL ON TABLE privilege FROM PUBLIC;
REVOKE ALL ON TABLE privilege FROM discs_ccdb;
GRANT ALL ON TABLE privilege TO discs_ccdb;
GRANT SELECT ON TABLE privilege TO backup_user;


--
-- TOC entry 2360 (class 0 OID 0)
-- Dependencies: 199
-- Name: privilege_privilege_id_seq; Type: ACL; Schema: public; Owner: discs_ccdb
--

REVOKE ALL ON SEQUENCE privilege_privilege_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE privilege_privilege_id_seq FROM discs_ccdb;
GRANT ALL ON SEQUENCE privilege_privilege_id_seq TO discs_ccdb;
GRANT SELECT ON SEQUENCE privilege_privilege_id_seq TO backup_user;


--
-- TOC entry 2361 (class 0 OID 0)
-- Dependencies: 202
-- Name: property; Type: ACL; Schema: public; Owner: discs_ccdb
--

REVOKE ALL ON TABLE property FROM PUBLIC;
REVOKE ALL ON TABLE property FROM discs_ccdb;
GRANT ALL ON TABLE property TO discs_ccdb;
GRANT SELECT ON TABLE property TO backup_user;


--
-- TOC entry 2363 (class 0 OID 0)
-- Dependencies: 201
-- Name: property_property_id_seq; Type: ACL; Schema: public; Owner: discs_ccdb
--

REVOKE ALL ON SEQUENCE property_property_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE property_property_id_seq FROM discs_ccdb;
GRANT ALL ON SEQUENCE property_property_id_seq TO discs_ccdb;
GRANT SELECT ON SEQUENCE property_property_id_seq TO backup_user;


--
-- TOC entry 2364 (class 0 OID 0)
-- Dependencies: 203
-- Name: role; Type: ACL; Schema: public; Owner: discs_ccdb
--

REVOKE ALL ON TABLE role FROM PUBLIC;
REVOKE ALL ON TABLE role FROM discs_ccdb;
GRANT ALL ON TABLE role TO discs_ccdb;
GRANT SELECT ON TABLE role TO backup_user;


--
-- TOC entry 2365 (class 0 OID 0)
-- Dependencies: 205
-- Name: slot; Type: ACL; Schema: public; Owner: discs_ccdb
--

REVOKE ALL ON TABLE slot FROM PUBLIC;
REVOKE ALL ON TABLE slot FROM discs_ccdb;
GRANT ALL ON TABLE slot TO discs_ccdb;
GRANT SELECT ON TABLE slot TO backup_user;


--
-- TOC entry 2366 (class 0 OID 0)
-- Dependencies: 207
-- Name: slot_artifact; Type: ACL; Schema: public; Owner: discs_ccdb
--

REVOKE ALL ON TABLE slot_artifact FROM PUBLIC;
REVOKE ALL ON TABLE slot_artifact FROM discs_ccdb;
GRANT ALL ON TABLE slot_artifact TO discs_ccdb;
GRANT SELECT ON TABLE slot_artifact TO backup_user;


--
-- TOC entry 2368 (class 0 OID 0)
-- Dependencies: 206
-- Name: slot_artifact_artifact_id_seq; Type: ACL; Schema: public; Owner: discs_ccdb
--

REVOKE ALL ON SEQUENCE slot_artifact_artifact_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE slot_artifact_artifact_id_seq FROM discs_ccdb;
GRANT ALL ON SEQUENCE slot_artifact_artifact_id_seq TO discs_ccdb;
GRANT SELECT ON SEQUENCE slot_artifact_artifact_id_seq TO backup_user;


--
-- TOC entry 2369 (class 0 OID 0)
-- Dependencies: 209
-- Name: slot_pair; Type: ACL; Schema: public; Owner: discs_ccdb
--

REVOKE ALL ON TABLE slot_pair FROM PUBLIC;
REVOKE ALL ON TABLE slot_pair FROM discs_ccdb;
GRANT ALL ON TABLE slot_pair TO discs_ccdb;
GRANT SELECT ON TABLE slot_pair TO backup_user;


--
-- TOC entry 2371 (class 0 OID 0)
-- Dependencies: 208
-- Name: slot_pair_slot_pair_id_seq; Type: ACL; Schema: public; Owner: discs_ccdb
--

REVOKE ALL ON SEQUENCE slot_pair_slot_pair_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE slot_pair_slot_pair_id_seq FROM discs_ccdb;
GRANT ALL ON SEQUENCE slot_pair_slot_pair_id_seq TO discs_ccdb;
GRANT SELECT ON SEQUENCE slot_pair_slot_pair_id_seq TO backup_user;


--
-- TOC entry 2372 (class 0 OID 0)
-- Dependencies: 211
-- Name: slot_property; Type: ACL; Schema: public; Owner: discs_ccdb
--

REVOKE ALL ON TABLE slot_property FROM PUBLIC;
REVOKE ALL ON TABLE slot_property FROM discs_ccdb;
GRANT ALL ON TABLE slot_property TO discs_ccdb;
GRANT SELECT ON TABLE slot_property TO backup_user;


--
-- TOC entry 2374 (class 0 OID 0)
-- Dependencies: 210
-- Name: slot_property_slot_prop_id_seq; Type: ACL; Schema: public; Owner: discs_ccdb
--

REVOKE ALL ON SEQUENCE slot_property_slot_prop_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE slot_property_slot_prop_id_seq FROM discs_ccdb;
GRANT ALL ON SEQUENCE slot_property_slot_prop_id_seq TO discs_ccdb;
GRANT SELECT ON SEQUENCE slot_property_slot_prop_id_seq TO backup_user;


--
-- TOC entry 2375 (class 0 OID 0)
-- Dependencies: 213
-- Name: slot_relation; Type: ACL; Schema: public; Owner: discs_ccdb
--

REVOKE ALL ON TABLE slot_relation FROM PUBLIC;
REVOKE ALL ON TABLE slot_relation FROM discs_ccdb;
GRANT ALL ON TABLE slot_relation TO discs_ccdb;
GRANT SELECT ON TABLE slot_relation TO backup_user;


--
-- TOC entry 2377 (class 0 OID 0)
-- Dependencies: 212
-- Name: slot_relation_slot_relation_id_seq; Type: ACL; Schema: public; Owner: discs_ccdb
--

REVOKE ALL ON SEQUENCE slot_relation_slot_relation_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE slot_relation_slot_relation_id_seq FROM discs_ccdb;
GRANT ALL ON SEQUENCE slot_relation_slot_relation_id_seq TO discs_ccdb;
GRANT SELECT ON SEQUENCE slot_relation_slot_relation_id_seq TO backup_user;


--
-- TOC entry 2379 (class 0 OID 0)
-- Dependencies: 204
-- Name: slot_slot_id_seq; Type: ACL; Schema: public; Owner: discs_ccdb
--

REVOKE ALL ON SEQUENCE slot_slot_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE slot_slot_id_seq FROM discs_ccdb;
GRANT ALL ON SEQUENCE slot_slot_id_seq TO discs_ccdb;
GRANT SELECT ON SEQUENCE slot_slot_id_seq TO backup_user;


--
-- TOC entry 2380 (class 0 OID 0)
-- Dependencies: 214
-- Name: unit; Type: ACL; Schema: public; Owner: discs_ccdb
--

REVOKE ALL ON TABLE unit FROM PUBLIC;
REVOKE ALL ON TABLE unit FROM discs_ccdb;
GRANT ALL ON TABLE unit TO discs_ccdb;
GRANT SELECT ON TABLE unit TO backup_user;


--
-- TOC entry 2381 (class 0 OID 0)
-- Dependencies: 170
-- Name: user; Type: ACL; Schema: public; Owner: discs_ccdb
--

REVOKE ALL ON TABLE "user" FROM PUBLIC;
REVOKE ALL ON TABLE "user" FROM discs_ccdb;
GRANT ALL ON TABLE "user" TO discs_ccdb;
GRANT SELECT ON TABLE "user" TO backup_user;


--
-- TOC entry 2382 (class 0 OID 0)
-- Dependencies: 216
-- Name: user_role; Type: ACL; Schema: public; Owner: discs_ccdb
--

REVOKE ALL ON TABLE user_role FROM PUBLIC;
REVOKE ALL ON TABLE user_role FROM discs_ccdb;
GRANT ALL ON TABLE user_role TO discs_ccdb;
GRANT SELECT ON TABLE user_role TO backup_user;


--
-- TOC entry 2384 (class 0 OID 0)
-- Dependencies: 215
-- Name: user_role_user_role_id_seq; Type: ACL; Schema: public; Owner: discs_ccdb
--

REVOKE ALL ON SEQUENCE user_role_user_role_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE user_role_user_role_id_seq FROM discs_ccdb;
GRANT ALL ON SEQUENCE user_role_user_role_id_seq TO discs_ccdb;
GRANT SELECT ON SEQUENCE user_role_user_role_id_seq TO backup_user;


-- Completed on 2014-06-03 13:27:42 CEST

--
-- PostgreSQL database dump complete
--

