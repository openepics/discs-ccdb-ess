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

SELECT pg_catalog.setval('alignment_record_alignment_record_id_seq', 2, true);


--
-- TOC entry 2249 (class 0 OID 41888)
-- Dependencies: 178
-- Data for Name: audit_record; Type: TABLE DATA; Schema: public; Owner: discs_ccdb
--

COPY audit_record (audit_record_id, entity_key, entity_type, entry, log_time, oper, "user") FROM stdin;
11  SL296   DEVICE  Modified device 2014-05-29 10:40:52 UPDATE  admin
12  SL298   DEVICE  Modified device 2014-05-29 10:41:37 UPDATE  admin
14  14cma08-001 DEVICE  Modified device 2014-05-29 10:46:46 UPDATE  admin
15  SC251   DEVICE  Modified device 2014-05-29 10:48:04 UPDATE  admin
16  14cma08-001 DEVICE  Modified device 2014-05-29 10:48:04 UPDATE  admin
17  SL303   DEVICE  Modified device 2014-05-29 10:48:33 UPDATE  admin
18  14cma08-001 DEVICE  Modified device 2014-05-29 10:48:33 UPDATE  admin
19  SC256   DEVICE  Modified device 2014-05-29 10:49:35 UPDATE  admin
20  14cma08-001 DEVICE  Modified device 2014-05-29 10:49:35 UPDATE  admin
37  SC255   DEVICE  Modified device 2014-05-29 10:56:35 UPDATE  admin
38  14cma08-001 DEVICE  Modified device 2014-05-29 10:56:35 UPDATE  admin
39  SC248   DEVICE  Modified device 2014-05-29 10:57:03 UPDATE  admin
40  14cma08-001 DEVICE  Modified device 2014-05-29 10:57:03 UPDATE  admin
41  SL298   DEVICE  Modified device 2014-05-29 10:57:32 UPDATE  admin
42  14cma08-001 DEVICE  Modified device 2014-05-29 10:57:32 UPDATE  admin
43  14cma08-001 DEVICE  Modified artifact ReA3 Cryomodule 3 Cold Mass  Alignment    2014-05-29 11:00:05 UPDATE  admin
44  FRIB    SLOT    Modified slot   2014-05-29 11:07:20 UPDATE  admin
45  FacilityRoot    SLOT    Modified slot   2014-05-29 11:07:45 UPDATE  admin
46  ReAccelerator   SLOT    Modified slot   2014-05-29 11:08:09 UPDATE  admin
47  FRIB    SLOT    Modified child slot 2014-05-29 11:08:40 UPDATE  admin
48  FacilityRoot    SLOT    Modified parent slot    2014-05-29 11:08:40 UPDATE  admin
49  ReAccelerator   SLOT    Modified child slot 2014-05-29 11:09:02 UPDATE  admin
50  FRIB    SLOT    Modified parent slot    2014-05-29 11:09:02 UPDATE  admin
51  CMA85   COMPONENT_TYPE  Updated artifact CAD Drawing    2014-05-29 11:24:05 UPDATE  admin
13  CMA85   COMPONENT_TYPE  Updated component type  2014-05-29 10:45:54 UPDATE  admin
1   CAV85   COMPONENT_TYPE  Updated component type  2014-05-29 10:34:36 UPDATE  admin
2   SC251   DEVICE  Modified device 2014-05-29 10:35:07 UPDATE  admin
3   SC256   DEVICE  Modified device 2014-05-29 10:35:42 UPDATE  admin
4   SC252   DEVICE  Modified device 2014-05-29 10:36:04 UPDATE  admin
5   SC253   DEVICE  Modified device 2014-05-29 10:36:19 UPDATE  admin
6   SC254   DEVICE  Modified device 2014-05-29 10:36:39 UPDATE  admin
7   SC255   DEVICE  Modified device 2014-05-29 10:36:53 UPDATE  admin
8   SC248   DEVICE  Modified device 2014-05-29 10:37:15 UPDATE  admin
9   SC249   DEVICE  Modified device 2014-05-29 10:37:32 UPDATE  admin
10  SL303   DEVICE  Modified device 2014-05-29 10:40:09 UPDATE  admin
21  SC252   DEVICE  Modified device 2014-05-29 10:50:18 UPDATE  admin
22  14cma08-001 DEVICE  Modified device 2014-05-29 10:50:18 UPDATE  admin
23  SC253   DEVICE  Modified device 2014-05-29 10:50:42 UPDATE  admin
24  14cma08-001 DEVICE  Modified device 2014-05-29 10:50:42 UPDATE  admin
25  SL296   DEVICE  Modified device 2014-05-29 10:51:21 UPDATE  admin
26  14cma08-001 DEVICE  Modified device 2014-05-29 10:51:21 UPDATE  admin
27  SL296   DEVICE  Modified device 2014-05-29 10:51:31 UPDATE  admin
28  14cma08-001 DEVICE  Modified device 2014-05-29 10:51:31 UPDATE  admin
29  SL296   DEVICE  Modified device 2014-05-29 10:52:09 UPDATE  admin
30  14cma08-001 DEVICE  Modified device 2014-05-29 10:52:09 UPDATE  admin
31  SL296   DEVICE  Modified device 2014-05-29 10:52:46 UPDATE  admin
32  14cma08-001 DEVICE  Modified device 2014-05-29 10:52:46 UPDATE  admin
33  SL296   DEVICE  Modified device 2014-05-29 10:55:43 UPDATE  admin
34  14cma08-001 DEVICE  Modified device 2014-05-29 10:55:43 UPDATE  admin
35  SC254   DEVICE  Modified device 2014-05-29 10:56:15 UPDATE  admin
36  14cma08-001 DEVICE  Modified device 2014-05-29 10:56:15 UPDATE  admin
\.


--
-- TOC entry 2388 (class 0 OID 0)
-- Dependencies: 177
-- Name: audit_record_audit_record_id_seq; Type: SEQUENCE SET; Schema: public; Owner: discs_ccdb
--

SELECT pg_catalog.setval('audit_record_audit_record_id_seq', 17, true);


--
-- TOC entry 2251 (class 0 OID 41899)
-- Dependencies: 180
-- Data for Name: component_type; Type: TABLE DATA; Schema: public; Owner: discs_ccdb
--

COPY component_type (component_type_id, description, modified_at, modified_by, name, version, super_component_type) FROM stdin;
1   Aperture    2014-04-29 06:38:02 test-user   AP  0   \N
2   Attenuation Plate (sieve to reduce beam intensity)  2014-04-29 03:03:55 test-user   ATP 0   \N
3   Beamline Gate Valve 2014-04-29 06:38:42 test-user   BGV 0   \N
4   Camera  2014-04-29 03:04:13 test-user   CAM 0   \N
5   rf cavity; inside cryomodule    2014-04-29 06:39:30 test-user   CAV 0   \N
6   Dipole magnets  2014-04-29 06:40:29 test-user   D   0   \N
7   Dipole magnet, Corrector, Horizontal    2014-04-29 03:14:40 test-user   DCH 0   \N
8   Dipole magnet, Corrector, Vertical  2014-04-29 03:14:52 test-user   DCV 0   \N
9   Device Drive, i.e. 45 Deg Slit  2014-04-29 06:41:16 test-user   DD  0   \N
10  Dipole Electric 2014-04-29 04:35:22 test-user   DE  0   \N
11  Dipole magnet, Horizontal   2014-04-29 06:42:15 test-user   DH  0   \N
12  Fast Acting Valve   2014-04-29 06:42:56 test-user   FAV 0   \N
13  Fast Acting Valve Sensor    2014-04-29 06:43:17 test-user   FAVS    0   \N
14  Faraday Cup 2014-04-29 03:03:43 test-user   FC  0   \N
15  Faraday Cup with Silicon Detector   2014-04-29 04:50:27 test-user   FCS 0   \N
16  Foil Silicon Detector   2014-04-29 06:44:04 test-user   FSD 0   \N
17  Gate Valve  2014-04-29 03:18:09 test-user   GV  0   \N
18  Gate valve 1    2014-04-29 06:59:55 test-user   GV1 0   \N
19  Gate valve 2    2014-04-29 07:00:12 test-user   GV2 0   \N
20  Multi Channel Plate combined with Viewer    2014-04-29 04:49:45 test-user   MCPV    0   \N
21  Multi-harmonic buncher  2014-04-29 06:45:10 test-user   MHB 0   \N
22  Power supply    2014-05-08 05:30:44 test-user   PS  0   \N
23  Power Supply, Bias  2014-04-29 06:46:43 test-user   PSB 0   \N
24  Quadrupole magnet   2014-04-29 06:47:28 test-user   Q   0   \N
25  Quadrupole, Electrostatic   2014-04-29 03:14:26 test-user   QE  0   \N
26  0.041 cavity    2014-05-12 14:29:47 test-user   QWR041  0   \N
27  rack    2014-05-09 10:15:00 test-user   RACK    0   \N
28  Radio Frequency Quadrupole      2014-04-29 06:48:57 test-user   RFQ 0   \N
29  Slit, Bottom Drive  2014-04-29 06:49:40 test-user   SLB 0   \N
30  Slit, Horizontal (used for beam selection in the horizontal plane)      2014-04-29 06:50:14 test-user   SLH 0   \N
31  Slit, Left Drive    2014-04-29 06:50:40 test-user   SLL 0   \N
32  Slit, Right Drive   2014-04-29 06:51:04 test-user   SLR 0   \N
33  Slit, Top Drive 2014-04-29 06:51:31 test-user   SLT 0   \N
34  Slit, Vertical (used for beam selection in the vertical plane)      2014-04-29 06:51:54 test-user   SLV 0   \N
35  Solenoid (within cryomodule), n is the solenoid number (baseline range 1 - 3)   2014-04-29 06:52:38 test-user   SOL 0   \N
36  Solenoid (beam line Room temperature solenoid)      2014-04-29 06:53:01 test-user   SOLR    0   \N
37  Solenoid (beam line Superconducting solenoid)   2014-04-29 06:53:35 test-user   SOLS    0   \N
38  Timing Detector with Silicon Detector   2014-04-29 06:54:16 test-user   TID 0   \N
39  Transfer line   2014-04-29 06:56:21 test-user   TL  0   \N
40  View detector   2014-04-29 03:04:59 test-user   VD  0   \N
41  Beamline device 2014-04-29 03:00:28 test-user   _BLD    0   \N
42  Diagnostics device  2014-04-29 03:03:29 test-user   _DIAG   0   \N
43  Magnet  2014-04-29 03:14:07 test-user   _MAG    0   \N
44  Power supply    2014-04-29 06:46:31 test-user   _PS 0   \N
45  Root of a facility  2014-05-08 03:45:09 test-user   _ROOT   0   \N
46  System  2014-05-08 03:47:05 test-user   _SYS    0   \N
47  Beta=0.085 quarter wave resonators  2014-05-29 06:34:36 test-user   CAV85   0   5
48  Coldmass Assembly Beta 0.085    2014-05-29 06:45:54 test-user   CMA85   0   \N
\.


--
-- TOC entry 2389 (class 0 OID 0)
-- Dependencies: 179
-- Name: component_type_component_type_id_seq; Type: SEQUENCE SET; Schema: public; Owner: discs_ccdb
--

SELECT pg_catalog.setval('component_type_component_type_id_seq', 1, true);


--
-- TOC entry 2253 (class 0 OID 41910)
-- Dependencies: 182
-- Data for Name: comptype_artifact; Type: TABLE DATA; Schema: public; Owner: discs_ccdb
--

COPY comptype_artifact (artifact_id, description, is_internal, modified_at, modified_by, name, uri, component_type) FROM stdin;
1   Mechanical Design drawing at Alfresco   f   2014-05-29 07:24:05 user    CAD Drawing https://docmgmt.nscl.msu.edu/share/page/site/NSCL/document-details?nodeRef=workspace://SpacesStore/6db52b14-b07f-4879-9641-8165372d9f26 48
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
1   f   2014-04-29 03:37:22 system      \N  0   41  2   \N
2   f   2014-04-29 03:40:46 system      \N  0   41  4   \N
3   f   2014-04-29 03:37:06 system      \N  0   41  9   \N
4   f   2014-04-29 03:37:52 system      \N  0   41  10  \N
5   f   2014-04-29 03:37:35 system      \N  0   41  14  \N
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
Aggregate   A mean value, standard deviation, and other meta data. Expresses the central tendency and dispersion of a set of data points    2014-02-26 00:00:00 system  t   0
boolean True or False   2014-02-26 00:00:00 system  t   0
byte    8 bit signed integer    2014-02-26 00:00:00 system  t   0
Continuum   Expersses a sequence of data points in time or frequency domain 2014-02-26 00:00:00 system  t   0
DOUBLE PRECISION    DOUBLE PRECISION precision IEEE 754 2014-02-26 00:00:00 system  t   0
Enum    An enumeration list and a value of that enumeration 2014-02-26 00:00:00 system  t   0
File    A sequence of bytes 2014-02-26 00:00:00 system  t   0
float   single precision IEEE 754   2014-02-26 00:00:00 system  t   0
Histogram   An array of real number intervals, and their frequency counts. Expresses a 1D histogram.    2014-02-26 00:00:00 system  t   0
Image   A general purpose pixel and meta data type, to encode image data of a single picture frame. 2014-02-26 00:00:00 system  t   0
int 32 bit signed integer   2014-02-26 00:00:00 system  t   0
long    64 bit signed integer   2014-02-26 00:00:00 system  t   0
Matrix  A real number matrix    2014-02-26 00:00:00 system  t   0
MultichannelArray   An array of PV names, their values, and metadata    2014-02-26 00:00:00 system  t   0
NameValue   An array of scalar values where each element is named   2014-02-26 00:00:00 system  t   0
ScalarArray An array of scalar values of some single type. Compare with NTVariantArray  2014-02-26 00:00:00 system  t   0
short   16 bit signed integer   2014-02-26 00:00:00 system  t   0
string  UTF-8   2014-02-26 00:00:00 system  t   0
Table   A table of scalars, where each column may be of different scalar array type 2014-02-26 00:00:00 system  t   0
uint    32 bit unsigned integer 2014-02-26 00:00:00 system  t   0
ulong   64 bit unsigned integer 2014-02-26 00:00:00 system  t   0
URI A structure for encapsulating a Uniform Resource Identifier (URI)   2014-02-26 00:00:00 system  t   0
UserDefined Defined by user 2014-02-26 00:00:00 system  t   0
ushort  16 bit unsigned integer     2014-02-26 00:00:00 system  t   0
VariantArray    An array of some scalar type, where the type and values may be changed  2014-02-26 00:00:00 system  t   0
\.


--
-- TOC entry 2261 (class 0 OID 41959)
-- Dependencies: 190
-- Data for Name: device; Type: TABLE DATA; Schema: public; Owner: discs_ccdb
--

COPY device (device_id, asm_description, asm_position, description, location, manuf_model, manuf_serial_number, manufacturer, modified_at, modified_by, purchase_order, serial_number, status, version, asm_parent, component_type, uuid) FROM stdin;
1   Cavity 1    C1  Beta=0.085 quarter wave resonators (ReA3 Cavities)  \N      \N      2014-05-29 06:48:04 vuppala     SC251   \N  0   12  47  \N
2   Cavity 2    C2  Beta=0.085 quarter wave resonators (ReA3 Cavities)  \N      \N      2014-05-29 06:49:35 vuppala     SC256   \N  0   12  47  \N
3   Cavity 3    C3  Beta=0.085 quarter wave resonators (ReA3 Cavities)  \N      \N      2014-05-29 06:50:18 vuppala     SC252   \N  0   12  47  \N
4   Cavity 4    C4  Beta=0.085 quarter wave resonators (ReA3 Cavities)  \N      \N      2014-05-29 06:50:42 vuppala     SC253   \N  0   12  47  \N
5   Cavity 5    C5  Beta=0.085 quarter wave resonators (ReA3 Cavities)  \N      \N      2014-05-29 06:56:15 vuppala     SC254   \N  0   12  47  \N
6   Cavity 6    C6  Beta=0.085 quarter wave resonators (ReA3 Cavities)  \N      \N      2014-05-29 06:56:35 vuppala     SC255   \N  0   12  47  \N
7   Cavity 7    C7  Beta=0.085 quarter wave resonators (ReA3 Cavities)  \N      \N      2014-05-29 06:57:03 vuppala     SC248   \N  0   12  47  \N
8   \N  \N  Beta=0.085 quarter wave resonators (ReA3 Cavities)  \N      \N      2014-05-29 06:37:32 vuppala     SC249   \N  0   \N  47  \N
9   Solenoid 1  S1  Solenoid    \N      \N      2014-05-29 06:48:33 vuppala     SL303   \N  0   12  35  \N
10  Solenoid 2  S2  Solenoid    \N      \N      2014-05-29 06:55:43 vuppala     SL296   \N  0   12  35  \N
11  Solenoid 3  S3  Solenoid    \N      \N      2014-05-29 06:57:32 vuppala     SL298   \N  0   12  35  \N
12  \N  \N  Coldmass Assembly 0.085 \N      \N      2014-05-29 06:57:32 vuppala     14cma08-001 \N  0   \N  48  \N
\.


--
-- TOC entry 2263 (class 0 OID 41970)
-- Dependencies: 192
-- Data for Name: device_artifact; Type: TABLE DATA; Schema: public; Owner: discs_ccdb
--

COPY device_artifact (artifact_id, description, is_internal, modified_at, modified_by, name, uri, device) FROM stdin;
1   ReA3 Cryomodule 3 Cold Mass \\r\\nAlignment f   2014-05-29 07:00:05 user    ReA3 Cryomodule 3 Cold Mass  Alignment  https://portal.frib.msu.edu/dcc/DCC Released Documents/M40101-TD-000556-R001.pdf    12
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

SELECT pg_catalog.setval('device_device_id_seq', 4, true);


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

SELECT pg_catalog.setval('installation_record_installation_record_id_seq', 17, true);


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
2   T   Accumulated center-to-center Length 2014-04-29 03:32:31 test-user   Accumulated-C2C-Length  0   float   meter
3   T   Radius aperture 2014-03-08 15:51:42 test-user   Aperture    0   float   meter
4   T   Position on the beamline    2014-04-29 03:40:23 test-user   Beamline-Position   0   int meter
5   T   Bending angle of a magnet   2014-03-08 15:50:36 test-user   Bending-Angle   0   float   degree (angle)
6   T   Bending radius  2014-03-08 15:52:28 test-user   Bending-Radius  0   float   meter
7   T   Current 2014-03-08 15:52:54 test-user   Current 0   float   ampere
8   T   Effective flange to flange length   2014-03-08 15:55:04 test-user   Effective-F2F-Length    0   float   meter
9   T   Effective length    2014-03-08 15:54:05 test-user   Effective-Length    0   float   meter
10  T   Length from element before (center to center)   2014-04-29 03:35:26 test-user   Length-From-Element-Before  0   float   meter
11  T   Maximum field of a magnet   2014-03-08 15:56:23 test-user   Maximum-Field   0   float   tesla
12  T   Minimum Deampipe innter diameter    2014-03-08 15:57:27 test-user   Min-Beampipe-Inner-Dia  0   float   meter
13  T   How many of such components are needed  2014-03-08 16:00:31 test-user   Number-Required 0   uint    ampere
14  T   An old name for a device    2014-04-29 03:34:20 test-user   Old-Name    0   string  ampere
15  T   Electric power  2014-03-08 15:58:19 test-user   Power   0   float   watt
16  T   Electric resistance 2014-03-08 16:01:40 test-user   Resistance  0   float   ampere
17  T   Electric voltage    2014-03-08 16:02:33 test-user   Voltage 0   float   volt
19  T   gradient    2014-05-09 10:12:48 test-user   Quad-Gradient   0   float   ampere-per-square-meter
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
816 \N  \N  \N  \N  Gate Valve  \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS10:BGV_D0862 0   \N  3   \N
817 \N  \N  \N  \N  Attenuator, Left    \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS10:ATP_D0863 0   \N  2   \N
818 \N  \N  \N  \N  Faraday Cup, Top    \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS10:FC_D0864  0   \N  14  \N
819 \N  \N  \N  \N  Camera  \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS10:CAM_D0864 0   \N  4   \N
820 \N  \N  \N  \N  Viewer Plate, Right \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS10:VD_D0864  0   \N  40  \N
821 \N  \N  \N  \N  Horizontal E-Steerer    \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS10:DCH_D0865 0   \N  7   \N
822 \N  \N  \N  \N  Vertical E-Steerer  \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS10:DCV_D0865 0   \N  8   \N
823 \N  \N  \N  \N  Triplet, EQuad A    \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS10:QE_D0867  0   \N  25  \N
824 \N  \N  \N  \N  Triplet, EQuad B    \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS10:QE_D0869  0   \N  25  \N
825 \N  \N  \N  \N  Triplet, EQuad C    \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS10:QE_D0871  0   \N  25  \N
826 \N  \N  \N  \N  Kicker66 for EBIT Injection \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS10:DE_D0873  0   \N  10  \N
827 \N  \N  \N  \N  Triplet, EQuad A    \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS10:QE_D0884  0   \N  25  \N
828 \N  \N  \N  \N  Triplet, EQuad B    \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS10:QE_D0886  0   \N  25  \N
829 \N  \N  \N  \N  Triplet, EQuad C    \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS10:QE_D0888  0   \N  25  \N
830 \N  \N  \N  \N  MultiChannel Plate, Top \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS10:MCPV_D0892    0   \N  20  \N
831 \N  \N  \N  \N  Camera  \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS10:CAM_D0892 0   \N  4   \N
832 \N  \N  \N  \N  Viewer Plate, Right \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS10:VD_D0892  0   \N  40  \N
833 \N  \N  \N  \N  Faraday Cup, Left   \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS10:FCS_D0893 0   \N  15  \N
834 \N  \N  \N  \N  Horizontal E-Steerer    \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS10:DCH_D0893 0   \N  7   \N
835 \N  \N  \N  \N  Vertical E-Steerer  \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS10:DCV_D0893 0   \N  8   \N
836 \N  \N  \N  \N  Gate Valve  \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS10:BGV_D0894 0   \N  3   \N
837 \N  \N  \N  \N  Cyl Dipole Deflector, 45deg, 681mm radius   \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS10:DE_D0900  0   \N  10  \N
838 \N  \N  \N  \N  Singlet, EQuad  \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS10:QE_D0905  0   \N  25  \N
839 \N  \N  \N  \N  Vertical E-Steerer  \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS10:DCV_D0905 0   \N  8   \N
840 \N  \N  \N  \N  Cyl Dipole Deflector, 45deg, 681mm radius   \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS10:DE_D0910  0   \N  10  \N
841 \N  \N  \N  \N  Faraday Cup, Left   \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS10:FCS_D0918 0   \N  15  \N
842 \N  \N  \N  \N  Camera  \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS10:CAM_D0918 0   \N  4   \N
843 \N  \N  \N  \N  Viewer Plate    \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS10:VD_D0918  0   \N  40  \N
844 \N  \N  \N  \N  Horizontal Slit, Right  \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS10:SLH_D0918 0   \N  30  \N
845 \N  \N  \N  \N  Horizontal E-Steerer    \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS10:DCH_D0919 0   \N  7   \N
846 \N  \N  \N  \N  Vertical E-Steerer  \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS10:DCV_D0919 0   \N  8   \N
847 \N  \N  \N  \N  DOUBLE PRECISIONt, EQuad A  \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS10:QE_D0921  0   \N  25  \N
848 \N  \N  \N  \N  DOUBLE PRECISIONt, Equad B  \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS10:QE_D0923  0   \N  25  \N
849 \N  \N  \N  \N  90deg Bend Magnet   \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS10:D_D0932   0   \N  6   \N
850 \N  \N  \N  \N  DOUBLE PRECISIONt, EQuad A  \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS10:QE_D0942  0   \N  25  \N
851 \N  \N  \N  \N  DOUBLE PRECISIONt, Equad B  \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS10:QE_D0944  0   \N  25  \N
852 \N  \N  \N  \N  MultiChannel Plate, Top \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS10:MCPV_D0947    0   \N  20  \N
853 \N  \N  \N  \N  Camera  \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS10:CAM_D0947 0   \N  4   \N
854 \N  \N  \N  \N  Viewer Plate    \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS10:VD_D0947  0   \N  40  \N
855 \N  \N  \N  \N  Faraday Cup, Left   \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS10:FCS_D0947 0   \N  15  \N
856 \N  \N  \N  \N  Horizontal Slit, Right  \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS10:SLH_D0947 0   \N  30  \N
857 \N  \N  \N  \N  Horizontal E-Steerer    \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS10:DCH_D0948 0   \N  7   \N
858 \N  \N  \N  \N  Vertical E-Steerer  \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS10:DCV_D0948 0   \N  8   \N
859 \N  \N  \N  \N  Gate Valve  \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS10:BGV_D0949 0   \N  3   \N
860 \N  \N  \N  \N  DOUBLE PRECISIONt, EQuad A  \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS10:QE_D0952  0   \N  25  \N
861 \N  \N  \N  \N  DOUBLE PRECISIONt, Equad B  \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS10:QE_D0954  0   \N  25  \N
862 \N  \N  \N  \N  DOUBLE PRECISIONt, EQuad A  \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS10:QE_D0958  0   \N  25  \N
863 \N  \N  \N  \N  DOUBLE PRECISIONt, Equad B  \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS10:QE_D0960  0   \N  25  \N
864 \N  \N  \N  \N  E-Deflector Plate, 120mm gap    \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS19:DE_D0970  0   \N  10  \N
865 \N  \N  \N  \N  Attenuator, Right   \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS19:ATP_D0974 0   \N  2   \N
866 \N  \N  \N  \N  Decay Counter   \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS19:FSD_D0975 0   \N  16  \N
867 \N  \N  \N  \N  Viewer Plate, Left  \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS19:VD_D0976  0   \N  40  \N
868 \N  \N  \N  \N  Camera, Right   \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS19:CAM_D0976 0   \N  4   \N
869 \N  \N  \N  \N  MultiChannel Plate, Bottom  \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS19:MCPV_D0976    0   \N  20  \N
870 \N  \N  \N  \N  Faraday Cup, Top    \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS19:FC_D0977  0   \N  14  \N
871 \N  \N  \N  \N  DOUBLE PRECISIONt, EQuad A  \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS19:QE_D0979  0   \N  25  \N
872 \N  \N  \N  \N  DOUBLE PRECISIONt, Equad B  \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS19:QE_D0981  0   \N  25  \N
873 \N  \N  \N  \N  DOUBLE PRECISIONt, EQuad A  \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS19:QE_D0985  0   \N  25  \N
874 \N  \N  \N  \N  DOUBLE PRECISIONt, Equad B  \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS19:QE_D0987  0   \N  25  \N
875 \N  \N  \N  \N  Viewer Plate, Left  \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS19:VD_D0990  0   \N  40  \N
876 \N  \N  \N  \N  Camera, Right   \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS19:CAM_D0990 0   \N  4   \N
877 \N  \N  \N  \N  MultiHarmonic Buncher   \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS19:MHB_D0993 0   \N  21  \N
878 \N  \N  \N  \N  0.7T Solenoid   \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS19:SOLR_D0996    0   \N  36  \N
879 \N  \N  \N  \N  45deg Slit Drive, Top-Right \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS19:DD_D0998  0   \N  9   \N
880 \N  \N  \N  \N  Faraday Cup, Top    \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS19:FC_D0999  0   \N  14  \N
881 \N  \N  \N  \N  Viewer Plate    \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS19:VD_D0999  0   \N  40  \N
882 \N  \N  \N  \N  Camera  \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS19:CAM_D0999 0   \N  4   \N
883 \N  \N  \N  \N  Timing Detector, Bottom \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS19:TID_D0999 0   \N  38  \N
884 \N  \N  \N  \N  Gate Valve  \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_RFQ:BGV_D0999   0   \N  3   \N
885 \N  \N  \N  \N  RFQ \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_RFQ:RFQ_D1017   0   \N  28  \N
886 \N  \N  \N  \N  Gate Valve  \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_RFQ:BGV_D1035   0   \N  3   \N
887 \N  \N  \N  \N  Horizontal Slit \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS20:SLH_D1037 0   \N  30  \N
888 \N  \N  \N  \N  Vertical Slit   \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS20:ATP_D1037 0   \N  2   \N
889 \N  \N  \N  \N  Vertical Slit   \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS20:SLV_D1038 0   \N  34  \N
890 \N  \N  \N  \N  45deg Slit Drive, Top-Right \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS20:DD_D1039  0   \N  9   \N
891 \N  \N  \N  \N  Timing Detector \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS20:TID_D1039 0   \N  38  \N
892 \N  \N  \N  \N  Faraday Cup \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS20:FC_D1039  0   \N  14  \N
893 \N  \N  \N  \N  Gate Valve  \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_CK01:BGV_D1042  0   \N  3   \N
894 \N  \N  \N  \N  Horizontal Mag. Steerer \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_CK01:DCH_D1045  0   \N  7   \N
895 \N  \N  \N  \N  Vertical Mag. Steerer   \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_CK01:DCV_D1045  0   \N  8   \N
896 \N  \N  \N  \N  9T SC Solenoid  \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_CK01:SOLS_D1045 0   \N  37  \N
897 \N  \N  \N  \N  Cavity, Beta = 0.041    \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_CK01:CAV_D1049  0   \N  5   \N
898 \N  \N  \N  \N  Horizontal Mag. Steerer \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_CK01:DCH_D1052  0   \N  7   \N
899 \N  \N  \N  \N  Vertical Mag. Steerer   \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_CK01:DCV_D1052  0   \N  8   \N
900 \N  \N  \N  \N  9T SC Solenoid  \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_CK01:SOLS_D1052 0   \N  37  \N
901 \N  \N  \N  \N  Gate Valve  \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_CK01:BGV_D1055  0   \N  3   \N
902 \N  \N  \N  \N  45deg Slit Drive    \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_WK01:DD_D1058   0   \N  9   \N
903 \N  \N  \N  \N  Foil Si Detector    \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_WK01:FSD_D1058  0   \N  16  \N
904 \N  \N  \N  \N  Defining Aperture   \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_WK01:AP_D1059   0   \N  1   \N
905 \N  \N  \N  \N  Timing Detector \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_WK01:TID_D1059  0   \N  38  \N
906 \N  \N  \N  \N  Faraday Cup \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_WK01:FC_D1059   0   \N  14  \N
907 \N  \N  \N  \N  Gate Valve  \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_CL01:BGV_D1062  0   \N  3   \N
908 \N  \N  \N  \N  Cavity, Beta = 0.041    \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_CL01:CAV_D1065  0   \N  5   \N
909 \N  \N  \N  \N  Horizontal Mag. Steerer \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_CL01:DCH_D1069  0   \N  7   \N
910 \N  \N  \N  \N  Vertical Mag. Steerer   \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_CL01:DCV_D1069  0   \N  8   \N
911 \N  \N  \N  \N  9T SC Solenoid  \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_CL01:SOL_D1069  0   \N  35  \N
912 \N  \N  \N  \N  Cavity, Beta = 0.041    \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_CL01:CAV_D1072  0   \N  5   \N
913 \N  \N  \N  \N  Cavity, Beta = 0.041    \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_CL01:CAV_D1075  0   \N  5   \N
914 \N  \N  \N  \N  Horizontal Mag. Steerer \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_CL01:DCH_D1078  0   \N  7   \N
915 \N  \N  \N  \N  Vertical Mag. Steerer   \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_CL01:DCV_D1078  0   \N  8   \N
916 \N  \N  \N  \N  9T SC Solenoid  \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_CL01:SOL_D1078  0   \N  35  \N
917 \N  \N  \N  \N  Cavity, Beta = 0.041    \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_CL01:CAV_D1082  0   \N  5   \N
918 \N  \N  \N  \N  Cavity, Beta = 0.041    \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_CL01:CAV_D1084  0   \N  5   \N
919 \N  \N  \N  \N  Horizontal Mag. Steerer \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_CL01:DCH_D1088  0   \N  7   \N
920 \N  \N  \N  \N  Vertical Mag. Steerer   \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_CL01:DCV_D1088  0   \N  8   \N
921 \N  \N  \N  \N  9T SC Solenoid  \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_CL01:SOL_D1088  0   \N  35  \N
922 \N  \N  \N  \N  Cavity, Beta = 0.041    \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_CL01:CAV_D1091  0   \N  5   \N
923 \N  \N  \N  \N  Gate Valve  \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_CL01:BGV_D1094  0   \N  3   \N
924 \N  \N  \N  \N  45deg Slit Drive    \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_WL01:DD_D1096   0   \N  9   \N
925 \N  \N  \N  \N  Foil Si Detector    \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_WL01:FSD_D1096  0   \N  16  \N
926 \N  \N  \N  \N  Timing Detector \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_WL01:TID_D1097  0   \N  38  \N
927 \N  \N  \N  \N  Faraday Cup \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_WL01:FC_D1097   0   \N  14  \N
928 \N  \N  \N  \N  Gate Valve  \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_CM01:BGV_D1099  0   \N  3   \N
929 \N  \N  \N  \N  Cavity, Beta = 0.085    \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_CM01:CAV_D1102  0   \N  5   \N
930 \N  \N  \N  \N  Horizontal Mag. Steerer \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_CM01:DCH_D1106  0   \N  7   \N
931 \N  \N  \N  \N  Vertical Mag. Steerer   \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_CM01:DCV_D1106  0   \N  8   \N
932 \N  \N  \N  \N  9T SC Solenoid  \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_CM01:SOL_D1106  0   \N  35  \N
933 \N  \N  \N  \N  Cavity, Beta = 0.085    \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_CM01:CAV_D1111  0   \N  5   \N
934 \N  \N  \N  \N  Cavity, Beta = 0.085    \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_CM01:CAV_D1115  0   \N  5   \N
935 \N  \N  \N  \N  Cavity, Beta = 0.085    \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_CM01:CAV_D1118  0   \N  5   \N
936 \N  \N  \N  \N  Horizontal Mag. Steerer \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_CM01:DCH_D1123  0   \N  7   \N
937 \N  \N  \N  \N  Vertical Mag. Steerer   \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_CM01:DCV_D1123  0   \N  8   \N
938 \N  \N  \N  \N  9T SC Solenoid  \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_CM01:SOL_D1123  0   \N  35  \N
939 \N  \N  \N  \N  Cavity, Beta = 0.085    \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_CM01:CAV_D1127  0   \N  5   \N
940 \N  \N  \N  \N  Cavity, Beta = 0.085    \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_CM01:CAV_D1131  0   \N  5   \N
941 \N  \N  \N  \N  Cavity, Beta = 0.085    \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_CM01:CAV_D1135  0   \N  5   \N
942 \N  \N  \N  \N  Horizontal Mag. Steerer \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_CM01:DCH_D1139  0   \N  7   \N
943 \N  \N  \N  \N  Vertical Mag. Steerer   \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_CM01:DCV_D1139  0   \N  8   \N
944 \N  \N  \N  \N  9T SC Solenoid  \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_CM01:SOL_D1139  0   \N  35  \N
945 \N  \N  \N  \N  Cavity, Beta = 0.085    \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_CM01:CAV_D1143  0   \N  5   \N
946 \N  \N  \N  \N  Gate Valve  \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_CM01:BGV_D1146  0   \N  3   \N
947 \N  \N  \N  \N  45deg Slit Drive    \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_WM01:DD_D1148   0   \N  9   \N
948 \N  \N  \N  \N  Foil Si Detector    \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_WM01:FSD_D1148  0   \N  16  \N
949 \N  \N  \N  \N  Timing Detector \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_WM01:TID_D1149  0   \N  38  \N
950 \N  \N  \N  \N  Faraday Cup \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_WM01:FC_D1149   0   \N  14  \N
951 \N  \N  \N  \N  Dipole magnet, vertical, RT \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS23:D_D1156   0   \N  6   \N
952 \N  \N  \N  \N  Gate valve  \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS25:BGV_D1161 0   \N  3   \N
953 \N  \N  \N  \N  Aperture    \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS24:AP_D1163  0   \N  1   \N
954 \N  \N  \N  \N  Faraday Cup \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS24:FC_D1164  0   \N  14  \N
955 \N  \N  \N  \N  Quad, RT, horizontal    \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS25:QH_D1164  0   \N  24  \N
956 \N  \N  \N  \N  Vertical Slit   \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS25:SLB_D1166 0   \N  29  \N
957 \N  \N  \N  \N  Vertical Slit   \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS25:SLT_D1166 0   \N  33  \N
958 \N  \N  \N  \N  Vertical Slit   \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS25:SLV_D1166 0   \N  34  \N
959 \N  \N  \N  \N  Quad, RT, vertical  \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS25:Q_D1169   0   \N  24  \N
960 \N  \N  \N  \N  Corrector, horizontal, RT   \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS25:DCH_D1172 0   \N  7   \N
961 \N  \N  \N  \N  Corrector, vertical, RT \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS25:DCV_D1172 0   \N  8   \N
962 \N  \N  \N  \N  Quad, RT, horizontal    \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS25:Q_D1174   0   \N  24  \N
963 \N  \N  \N  \N  Viewer Plate    \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS25:VD_D1178  0   \N  40  \N
964 \N  \N  \N  \N  Camera  \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS25:CAM_D1178 0   \N  4   \N
965 \N  \N  \N  \N  Faraday Cup \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS25:FC_D1178  0   \N  14  \N
966 \N  \N  \N  \N  Sensor fast gate valve  \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS25:FAVS_D1178    0   \N  13  \N
967 \N  \N  \N  \N  Quad, RT, horizontal    \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS25:Q_D1182   0   \N  24  \N
968 \N  \N  \N  \N  Quad, RT, vertical  \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS25:Q_D1186   0   \N  24  \N
969 \N  \N  \N  \N  Quad, RT, horizontal    \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS25:Q_D1192   0   \N  24  \N
970 \N  \N  \N  \N  Dipole magnet, vertical, RT \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS25:D_D1200   0   \N  6   \N
971 \N  \N  \N  \N  Gate valve  \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS25:BGV_D1205 0   \N  3   \N
972 \N  \N  \N  \N  Timing Detector \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS25:TID_D1207 0   \N  38  \N
973 \N  \N  \N  \N  45deg Slit Drive    \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS25:DD_D1207  0   \N  9   \N
974 \N  \N  \N  \N  Foil Si Detector    \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS25:FSD_D1207 0   \N  16  \N
975 \N  \N  \N  \N  Faraday Cup \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS25:FC_D1207  0   \N  14  \N
976 \N  \N  \N  \N  Sensor fast gate valve  \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS25:FAVS_D1207    0   \N  13  \N
977 \N  \N  \N  \N  Timing Detector \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS25:TID_D1212 0   \N  38  \N
978 \N  \N  \N  \N  Corrector, horizontal, RT   \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS28:DCH_D1219 0   \N  7   \N
979 \N  \N  \N  \N  Corrector, vertical, RT \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS28:DCV_D1219 0   \N  8   \N
980 \N  \N  \N  \N  Quad, RT, horizontal    \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS28:Q_D1221   0   \N  24  \N
981 \N  \N  \N  \N  Quad, RT, vertical  \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS28:Q_D1228   0   \N  24  \N
982 \N  \N  \N  \N  Quad, RT, horizontal    \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS28:Q_D1245   0   \N  24  \N
983 \N  \N  \N  \N  Quad, RT, vertical  \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS28:Q_D1252   0   \N  24  \N
984 \N  \N  \N  \N  Viewer Plate    \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS28:VD_D1256  0   \N  40  \N
985 \N  \N  \N  \N  Camera  \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS28:CAM_D1256 0   \N  4   \N
986 \N  \N  \N  \N  4 jaw collimator    \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS28:SLB_D1256 0   \N  29  \N
987 \N  \N  \N  \N  4 jaw collimator    \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS28:SLL_D1256 0   \N  31  \N
988 \N  \N  \N  \N  4 jaw collimator    \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS28:SLR_D1256 0   \N  32  \N
989 \N  \N  \N  \N  4 jaw collimator    \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS28:SLT_D1256 0   \N  33  \N
990 \N  \N  \N  \N  RIB diag. (diamond) \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS28:TID_D1256 0   \N  38  \N
991 \N  \N  \N  \N  45 deg Slit Drive   \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS28:DD_D1256  0   \N  9   \N
992 \N  \N  \N  \N  Faraday Cup \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS28:FC_D1256  0   \N  14  \N
993 \N  \N  \N  \N  Decay Counter   \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS28:FSD_D1256 0   \N  16  \N
994 \N  \N  \N  \N  Corrector, horizontal, RT   \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS28:DCH_D1270 0   \N  7   \N
995 \N  \N  \N  \N  Corrector, vertical, RT \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS28:DCV_D1270 0   \N  8   \N
996 \N  \N  \N  \N  Quad, RT, horizontal    \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS28:Q_D1272   0   \N  24  \N
997 \N  \N  \N  \N  Quad, RT, vertical  \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS28:Q_D1275   0   \N  24  \N
998 \N  \N  \N  \N  Quad, RT, vertical  \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS28:Q_D1281   0   \N  24  \N
999 \N  \N  \N  \N  Quad, RT, horizontal    \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS30:Q_D1285   0   \N  24  \N
1000    \N  \N  \N  \N  Decay Counter   \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS28:FSD_D1288 0   \N  16  \N
1001    \N  \N  \N  \N  Dipole magnet, horizontal, RT   \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS28:D_D1296   0   \N  6   \N
1002    \N  \N  \N  \N  Gate valve  \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS30:BGV_D1301 0   \N  3   \N
1003    \N  \N  \N  \N  Fast Acting Gate valve  \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS30:FAV_D1301 0   \N  12  \N
1004    \N  \N  \N  \N  Corrector, horizontal, RT   \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS30:DCH_D1305 0   \N  7   \N
1005    \N  \N  \N  \N  Corrector, vertical, RT \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS30:DCV_D1305 0   \N  8   \N
1006    \N  \N  \N  \N  Quad, RT, vertical  \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS30:Q_D1307   0   \N  24  \N
1007    \N  \N  \N  \N  Quad, RT, horizontal    \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS30:Q_D1310   0   \N  24  \N
1008    \N  \N  \N  \N  Horizontal Slit \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS30:SLL_D1316 0   \N  31  \N
1009    \N  \N  \N  \N  Horizontal Slit \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS30:SLR_D1316 0   \N  32  \N
1010    \N  \N  \N  \N  Viewer Plate    \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS30:VD_D1316  0   \N  40  \N
1011    \N  \N  \N  \N  Camera  \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS30:CAM_D1316 0   \N  4   \N
1012    \N  \N  \N  \N  Timing Detector \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS30:TID_D1316 0   \N  38  \N
1013    \N  \N  \N  \N  Faraday Cup \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS30:FC_D1316  0   \N  14  \N
1014    \N  \N  \N  \N  Decay Counter   \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS30:FSD_D1316 0   \N  16  \N
1015    \N  \N  \N  \N  Quad, RT, horizontal    \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS30:Q_D1323   0   \N  24  \N
1016    \N  \N  \N  \N  Quad, RT, vertical  \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS30:Q_D1327   0   \N  24  \N
1017    \N  \N  \N  \N  Dipole magnet, horizontal, RT   \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS30:D_D1338   0   \N  6   \N
1018    \N  \N  \N  \N  Corrector, horizontal, RT   \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS30:DCH_D1345 0   \N  7   \N
1019    \N  \N  \N  \N  Corrector, vertical, RT \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS30:DCV_D1345 0   \N  8   \N
1020    \N  \N  \N  \N  Quad, RT, horizontal    \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS30:Q_D1346   0   \N  24  \N
1021    \N  \N  \N  \N  Quad, RT, vertical  \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS30:Q_D1351   0   \N  24  \N
1022    \N  \N  \N  \N  Faraday Cup \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS30:FC_D1355  0   \N  14  \N
1023    \N  \N  \N  \N  Sensor fast gate valve  \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS30:FAVS_D1355    0   \N  13  \N
1024    \N  \N  \N  \N  Dipole magnet, horizontal, RT   \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS30:DH_D1362  0   \N  11  \N
1025    \N  \N  \N  \N  Gate valve  \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS31:BGV_D1367 0   \N  3   \N
1026    \N  \N  \N  \N  Quad, RT, horizonal \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS32:Q_D1369   0   \N  24  \N
1027    \N  \N  \N  \N  Quad, RT, vertical  \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS31:Q_D1374   0   \N  24  \N
1028    \N  \N  \N  \N  Dipole magnet, horizontal, RT   \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS32:DH_D1376  0   \N  11  \N
1029    \N  \N  \N  \N  Gate valve  \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS33:BGV_D1381 0   \N  3   \N
1030    \N  \N  \N  \N  Quad, RT, horizontal    \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS31:Q_D1381   0   \N  24  \N
1031    \N  \N  \N  \N  Gate valve  \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS34:BGV_D1381 0   \N  3   \N
1032    \N  \N  \N  \N  Corrector, horizontal, RT   \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS33:DCH_D1385 0   \N  7   \N
1033    \N  \N  \N  \N  Corrector, vertical, RT \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS33:DCV_D1385 0   \N  8   \N
1034    \N  \N  \N  \N  Quad, RT, vertical  \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS34:Q_D1387   0   \N  24  \N
1035    \N  \N  \N  \N  Quad, RT, vertical  \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS33:Q_D1388   0   \N  24  \N
1036    \N  \N  \N  \N  Quad, RT, horizontal    \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS34:Q_D1390   0   \N  24  \N
1037    \N  \N  \N  \N  Corrector, horizontal, RT   \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS31:DCH_D1393 0   \N  7   \N
1038    \N  \N  \N  \N  Corrector, vertical, RT \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS31:DCV_D1393 0   \N  8   \N
1039    \N  \N  \N  \N  Quad, RT, horizontal    \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS33:Q_D1395   0   \N  24  \N
1040    \N  \N  \N  \N  Quad, RT, vertical  \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS31:Q_D1397   0   \N  24  \N
1041    \N  \N  \N  \N  Corrector, horizontal, RT   \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS33:DCH_D1404 0   \N  7   \N
1042    \N  \N  \N  \N  Corrector, vertical, RT \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS33:DCV_D1404 0   \N  8   \N
1043    \N  \N  \N  \N  Aperture    \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS33:AP_D1408  0   \N  1   \N
1044    \N  \N  \N  \N  Quad, RT, horizontal    \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS31:Q_D1409   0   \N  24  \N
1045    \N  \N  \N  \N  Quad, RT, horizontal    \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS33:Q_D1411   0   \N  24  \N
1046    \N  \N  \N  \N  Corrector, horizontal, RT   \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS34:DCH_D1413 0   \N  7   \N
1047    \N  \N  \N  \N  Corrector, vertical, RT \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS34:DCV_D1413 0   \N  8   \N
1048    \N  \N  \N  \N  Quad, RT, vertical  \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS33:Q_D1415   0   \N  24  \N
1049    \N  \N  \N  \N  Quad, RT, vertical  \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS34:Q_D1415   0   \N  24  \N
1050    \N  \N  \N  \N  Faraday Cup \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS33:FC_D1420  0   \N  14  \N
1051    \N  \N  \N  \N  Aperture    \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS33:AP_D1420  0   \N  1   \N
1052    \N  \N  \N  \N  Viewer Plate    \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS33:VD_D1420  0   \N  40  \N
1053    \N  \N  \N  \N  Camera  \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS33:CAM_D1420 0   \N  4   \N
1054    \N  \N  \N  \N  Decay Counter   \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS33:FSD_D1420 0   \N  16  \N
1055    \N  \N  \N  \N  Gate valve  \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS33:BGV_D1422 0   \N  3   \N
1056    \N  \N  \N  \N  Corrector, horizontal, RT   \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS31:DCH_D1423 0   \N  7   \N
1057    \N  \N  \N  \N  Corrector, vertical, RT \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS31:DCV_D1423 0   \N  8   \N
1058    \N  \N  \N  \N  Aperture    \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS31:AP_D1427  0   \N  1   \N
1059    \N  \N  \N  \N  Quad, RT, vertical  \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS34:Q_D1427   0   \N  24  \N
1060    \N  \N  \N  \N  Quad, RT, horizontal    \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS31:Q_D1431   0   \N  24  \N
1061    \N  \N  \N  \N  Corrector, horizontal, RT   \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS34:DCH_D1431 0   \N  7   \N
1062    \N  \N  \N  \N  Corrector, vertical, RT \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS34:DCV_D1431 0   \N  8   \N
1063    \N  \N  \N  \N  Quad, RT, vertical  \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS31:Q_D1435   0   \N  24  \N
1064    \N  \N  \N  \N  Aperture    \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS34:AP_D1435  0   \N  1   \N
1065    \N  \N  \N  \N  ANASEN/CFFD \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer DANS:TL_D1439   0   \N  39  \N
1066    \N  \N  \N  \N  Faraday Cup \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS31:FC_D1439  0   \N  14  \N
1067    \N  \N  \N  \N  Aperture    \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS31:AP_D1439  0   \N  1   \N
1068    \N  \N  \N  \N  Viewer Plate    \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS31:VD_D1439  0   \N  40  \N
1069    \N  \N  \N  \N  Camera  \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS31:CAM_D1439 0   \N  4   \N
1070    \N  \N  \N  \N  Decay Counter   \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS31:FSD_D1439 0   \N  16  \N
1071    \N  \N  \N  \N  Quad, RT, horizontal    \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS34:Q_D1439   0   \N  24  \N
1072    \N  \N  \N  \N  Gate valve  \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS31:BGV_D1441 0   \N  3   \N
1073    \N  \N  \N  \N  Quad, RT, vertical  \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS34:Q_D1443   0   \N  24  \N
1074    \N  \N  \N  \N  Faraday Cup \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS34:FC_D1448  0   \N  14  \N
1075    \N  \N  \N  \N  Aperture    \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS34:AP_D1448  0   \N  1   \N
1076    \N  \N  \N  \N  Viewer Plate    \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS34:VD_D1448  0   \N  40  \N
1077    \N  \N  \N  \N  Camera  \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS34:CAM_D1448 0   \N  4   \N
1078    \N  \N  \N  \N  Decay Counter   \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS34:FSD_D1448 0   \N  16  \N
1079    \N  \N  \N  \N  Gate valve  \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS34:BGV_D1450 0   \N  3   \N
1080    \N  \N  \N  \N  Faraday Cup \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS34:FC_D1458  0   \N  14  \N
1081    \N  \N  \N  \N  AT-TPC  \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer DATP:TL_D1460   0   \N  39  \N
1082    \N  \N  \N  \N  Faraday Cup \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS34:FC_D1460  0   \N  14  \N
1083    \N  \N  \N  \N  Viewer Plate    \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS34:VD_D1464  0   \N  40  \N
1084    \N  \N  \N  \N  Camera  \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS34:CAM_D1464 0   \N  4   \N
1085    \N  \N  \N  \N  SECAR/JENSA \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer DJNS:TL_D1465   0   \N  39  \N
1086    \N  \N  \N  \N  Faraday Cup \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS34:FC_D1477  0   \N  14  \N
1087    \N  \N  \N  \N  Decay Counter   \N  \N  \N  \N  \N  \N  f   2014-05-02 00:00:00 wittmer REA_BTS34:FSD_D1477 0   \N  16  \N
1088    \N  \N  \N  \N  Facility for Rare Isotope Beam  \N  \N  \N  \N  \N  \N  f   2014-05-29 07:07:20 user    FRIB    0   \N  46  \N
1089    \N  \N  \N  \N  Facility Root Node  \N  \N  \N  \N  \N  \N  f   2014-05-29 07:07:45 user    FacilityRoot    0   \N  45  \N
1090    \N  \N  \N  \N  ReA \N  \N  \N  \N  \N  \N  f   2014-05-29 07:08:09 user    ReAccelerator   0   \N  46  \N
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
1   0   1088    1089    1
2   0   1090    1088    1
3   0   816 1090    1
4   0   817 1090    1
5   0   818 1090    1
6   0   819 1090    1
7   0   820 1090    1
8   0   821 1090    1
9   0   822 1090    1
10  0   823 1090    1
11  0   824 1090    1
12  0   825 1090    1
13  0   826 1090    1
14  0   827 1090    1
15  0   828 1090    1
16  0   829 1090    1
17  0   830 1090    1
18  0   831 1090    1
19  0   832 1090    1
20  0   833 1090    1
21  0   834 1090    1
22  0   835 1090    1
23  0   836 1090    1
24  0   837 1090    1
25  0   838 1090    1
26  0   839 1090    1
27  0   840 1090    1
28  0   841 1090    1
29  0   842 1090    1
30  0   843 1090    1
31  0   844 1090    1
32  0   845 1090    1
33  0   846 1090    1
34  0   847 1090    1
35  0   848 1090    1
36  0   849 1090    1
37  0   850 1090    1
38  0   851 1090    1
39  0   852 1090    1
40  0   853 1090    1
41  0   854 1090    1
42  0   855 1090    1
43  0   856 1090    1
44  0   857 1090    1
45  0   858 1090    1
46  0   859 1090    1
47  0   860 1090    1
48  0   861 1090    1
49  0   862 1090    1
50  0   863 1090    1
51  0   864 1090    1
52  0   865 1090    1
53  0   866 1090    1
54  0   867 1090    1
55  0   868 1090    1
56  0   869 1090    1
57  0   870 1090    1
58  0   871 1090    1
59  0   872 1090    1
60  0   873 1090    1
61  0   874 1090    1
62  0   875 1090    1
63  0   876 1090    1
64  0   877 1090    1
65  0   878 1090    1
66  0   879 1090    1
67  0   880 1090    1
68  0   881 1090    1
69  0   882 1090    1
70  0   883 1090    1
71  0   884 1090    1
72  0   885 1090    1
73  0   886 1090    1
74  0   887 1090    1
75  0   888 1090    1
76  0   889 1090    1
77  0   890 1090    1
78  0   891 1090    1
79  0   892 1090    1
80  0   893 1090    1
81  0   894 1090    1
82  0   895 1090    1
83  0   896 1090    1
84  0   897 1090    1
85  0   898 1090    1
86  0   899 1090    1
87  0   900 1090    1
88  0   901 1090    1
89  0   902 1090    1
90  0   903 1090    1
91  0   904 1090    1
92  0   905 1090    1
93  0   906 1090    1
94  0   907 1090    1
95  0   908 1090    1
96  0   909 1090    1
97  0   910 1090    1
98  0   911 1090    1
99  0   912 1090    1
100 0   913 1090    1
101 0   914 1090    1
102 0   915 1090    1
103 0   916 1090    1
104 0   917 1090    1
105 0   918 1090    1
106 0   919 1090    1
107 0   920 1090    1
108 0   921 1090    1
109 0   922 1090    1
110 0   923 1090    1
111 0   924 1090    1
112 0   925 1090    1
113 0   926 1090    1
114 0   927 1090    1
115 0   928 1090    1
116 0   929 1090    1
117 0   930 1090    1
118 0   931 1090    1
119 0   932 1090    1
120 0   933 1090    1
121 0   934 1090    1
122 0   935 1090    1
123 0   936 1090    1
124 0   937 1090    1
125 0   938 1090    1
126 0   939 1090    1
127 0   940 1090    1
128 0   941 1090    1
129 0   942 1090    1
130 0   943 1090    1
131 0   944 1090    1
132 0   945 1090    1
133 0   946 1090    1
134 0   947 1090    1
135 0   948 1090    1
136 0   949 1090    1
137 0   950 1090    1
138 0   951 1090    1
139 0   952 1090    1
140 0   953 1090    1
141 0   954 1090    1
142 0   955 1090    1
143 0   956 1090    1
144 0   957 1090    1
145 0   958 1090    1
146 0   959 1090    1
147 0   960 1090    1
148 0   961 1090    1
149 0   962 1090    1
150 0   963 1090    1
151 0   964 1090    1
152 0   965 1090    1
153 0   966 1090    1
154 0   967 1090    1
155 0   968 1090    1
156 0   969 1090    1
157 0   970 1090    1
158 0   971 1090    1
159 0   972 1090    1
160 0   973 1090    1
161 0   974 1090    1
162 0   975 1090    1
163 0   976 1090    1
164 0   977 1090    1
165 0   978 1090    1
166 0   979 1090    1
167 0   980 1090    1
168 0   981 1090    1
169 0   982 1090    1
170 0   983 1090    1
171 0   984 1090    1
172 0   985 1090    1
173 0   986 1090    1
174 0   987 1090    1
175 0   988 1090    1
176 0   989 1090    1
177 0   990 1090    1
178 0   991 1090    1
179 0   992 1090    1
180 0   993 1090    1
181 0   994 1090    1
182 0   995 1090    1
183 0   996 1090    1
184 0   997 1090    1
185 0   998 1090    1
186 0   999 1090    1
187 0   1000    1090    1
188 0   1001    1090    1
189 0   1002    1090    1
190 0   1003    1090    1
191 0   1004    1090    1
192 0   1005    1090    1
193 0   1006    1090    1
194 0   1007    1090    1
195 0   1008    1090    1
196 0   1009    1090    1
197 0   1010    1090    1
198 0   1011    1090    1
199 0   1012    1090    1
200 0   1013    1090    1
201 0   1014    1090    1
202 0   1015    1090    1
203 0   1016    1090    1
204 0   1017    1090    1
205 0   1018    1090    1
206 0   1019    1090    1
207 0   1020    1090    1
208 0   1021    1090    1
209 0   1022    1090    1
210 0   1023    1090    1
211 0   1024    1090    1
212 0   1025    1090    1
213 0   1026    1090    1
214 0   1027    1090    1
215 0   1028    1090    1
216 0   1029    1090    1
217 0   1030    1090    1
218 0   1031    1090    1
219 0   1032    1090    1
220 0   1033    1090    1
221 0   1034    1090    1
222 0   1035    1090    1
223 0   1036    1090    1
224 0   1037    1090    1
225 0   1038    1090    1
226 0   1039    1090    1
227 0   1040    1090    1
228 0   1041    1090    1
229 0   1042    1090    1
230 0   1043    1090    1
231 0   1044    1090    1
232 0   1045    1090    1
233 0   1046    1090    1
234 0   1047    1090    1
235 0   1048    1090    1
236 0   1049    1090    1
237 0   1050    1090    1
238 0   1051    1090    1
239 0   1052    1090    1
240 0   1053    1090    1
241 0   1054    1090    1
242 0   1055    1090    1
243 0   1056    1090    1
244 0   1057    1090    1
245 0   1058    1090    1
246 0   1059    1090    1
247 0   1060    1090    1
248 0   1061    1090    1
249 0   1062    1090    1
250 0   1063    1090    1
251 0   1064    1090    1
252 0   1065    1090    1
253 0   1066    1090    1
254 0   1067    1090    1
255 0   1068    1090    1
256 0   1069    1090    1
257 0   1070    1090    1
258 0   1071    1090    1
259 0   1072    1090    1
260 0   1073    1090    1
261 0   1074    1090    1
262 0   1075    1090    1
263 0   1076    1090    1
264 0   1077    1090    1
265 0   1078    1090    1
266 0   1079    1090    1
267 0   1080    1090    1
268 0   1081    1090    1
269 0   1082    1090    1
270 0   1083    1090    1
271 0   1084    1090    1
272 0   1085    1090    1
273 0   1086    1090    1
274 0   1087    1090    1
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
1   f   2014-05-02  wittmer 86.242125   0   2   816 \N
2   f   2014-05-02  wittmer 0862    0   4   816 \N
3   f   2014-05-02  wittmer 0.035000    0   9   816 \N
4   f   2014-05-02  wittmer 0.134925    0   10  816 \N
5   f   2014-05-02  wittmer L014GV  0   14  816 \N
6   f   2014-05-02  wittmer 86.338696   0   2   817 \N
7   f   2014-05-02  wittmer 0863    0   4   817 \N
8   f   2014-05-02  wittmer -0.038354   0   10  817 \N
9   f   2014-05-02  wittmer L016A   0   14  817 \N
10  f   2014-05-02  wittmer 86.394881   0   2   818 \N
11  f   2014-05-02  wittmer 0864    0   4   818 \N
12  f   2014-05-02  wittmer 0.017831    0   10  818 \N
13  f   2014-05-02  wittmer L016FC  0   14  818 \N
14  f   2014-05-02  wittmer 86.377050   0   2   819 \N
15  f   2014-05-02  wittmer 0864    0   4   819 \N
16  f   2014-05-02  wittmer 0.000000    0   10  819 \N
17  f   2014-05-02  wittmer L016VC  0   14  819 \N
18  f   2014-05-02  wittmer 86.377050   0   2   820 \N
19  f   2014-05-02  wittmer 0864    0   4   820 \N
20  f   2014-05-02  wittmer 0.000000    0   10  820 \N
21  f   2014-05-02  wittmer L016VP  0   14  820 \N
22  f   2014-05-02  wittmer 86.463079   0   2   821 \N
23  f   2014-05-02  wittmer 0865    0   4   821 \N
24  f   2014-05-02  wittmer 0.060000    0   9   821 \N
25  f   2014-05-02  wittmer 0.086030    0   10  821 \N
26  f   2014-05-02  wittmer L016DH  0   14  821 \N
27  f   2014-05-02  wittmer 86.463079   0   2   822 \N
28  f   2014-05-02  wittmer 0865    0   4   822 \N
29  f   2014-05-02  wittmer 0.060000    0   9   822 \N
30  f   2014-05-02  wittmer L016DV  0   14  822 \N
31  f   2014-05-02  wittmer 86.711047   0   2   823 \N
32  f   2014-05-02  wittmer 0867    0   4   823 \N
33  f   2014-05-02  wittmer 0.150000    0   9   823 \N
34  f   2014-05-02  wittmer 0.247968    0   10  823 \N
35  f   2014-05-02  wittmer L018TA  0   14  823 \N
36  f   2014-05-02  wittmer 86.886053   0   2   824 \N
37  f   2014-05-02  wittmer 0869    0   4   824 \N
38  f   2014-05-02  wittmer 0.150000    0   9   824 \N
39  f   2014-05-02  wittmer 0.175006    0   10  824 \N
40  f   2014-05-02  wittmer L018TB  0   14  824 \N
41  f   2014-05-02  wittmer 87.061059   0   2   825 \N
42  f   2014-05-02  wittmer 0871    0   4   825 \N
43  f   2014-05-02  wittmer 0.150000    0   9   825 \N
44  f   2014-05-02  wittmer 0.175006    0   10  825 \N
45  f   2014-05-02  wittmer L018TC  0   14  825 \N
46  f   2014-05-02  wittmer 87.298090   0   2   826 \N
47  f   2014-05-02  wittmer 0873    0   4   826 \N
48  f   2014-05-02  wittmer 0.138000    0   9   826 \N
49  f   2014-05-02  wittmer 0.237031    0   10  826 \N
50  f   2014-05-02  wittmer L019DE  0   14  826 \N
51  f   2014-05-02  wittmer 88.397721   0   2   827 \N
52  f   2014-05-02  wittmer 0884    0   4   827 \N
53  f   2014-05-02  wittmer 0.150000    0   9   827 \N
54  f   2014-05-02  wittmer 1.099631    0   10  827 \N
55  f   2014-05-02  wittmer L024TA  0   14  827 \N
56  f   2014-05-02  wittmer 88.572727   0   2   828 \N
57  f   2014-05-02  wittmer 0886    0   4   828 \N
58  f   2014-05-02  wittmer 0.150000    0   9   828 \N
59  f   2014-05-02  wittmer 0.175006    0   10  828 \N
60  f   2014-05-02  wittmer L024TB  0   14  828 \N
61  f   2014-05-02  wittmer 88.747733   0   2   829 \N
62  f   2014-05-02  wittmer 0888    0   4   829 \N
63  f   2014-05-02  wittmer 0.150000    0   9   829 \N
64  f   2014-05-02  wittmer 0.175006    0   10  829 \N
65  f   2014-05-02  wittmer L024TC  0   14  829 \N
66  f   2014-05-02  wittmer 89.209365   0   2   830 \N
67  f   2014-05-02  wittmer 0892    0   4   830 \N
68  f   2014-05-02  wittmer -0.024575   0   10  830 \N
69  f   2014-05-02  wittmer L026MCP 0   14  830 \N
70  f   2014-05-02  wittmer 89.233940   0   2   831 \N
71  f   2014-05-02  wittmer 0892    0   4   831 \N
72  f   2014-05-02  wittmer 0.000000    0   10  831 \N
73  f   2014-05-02  wittmer L026VC  0   14  831 \N
74  f   2014-05-02  wittmer 89.233940   0   2   832 \N
75  f   2014-05-02  wittmer 0892    0   4   832 \N
76  f   2014-05-02  wittmer 0.486207    0   10  832 \N
77  f   2014-05-02  wittmer L026VP  0   14  832 \N
78  f   2014-05-02  wittmer 89.251771   0   2   833 \N
79  f   2014-05-02  wittmer 0893    0   4   833 \N
80  f   2014-05-02  wittmer 0.017831    0   10  833 \N
81  f   2014-05-02  wittmer L026FC  0   14  833 \N
82  f   2014-05-02  wittmer 89.319970   0   2   834 \N
83  f   2014-05-02  wittmer 0893    0   4   834 \N
84  f   2014-05-02  wittmer 0.060000    0   9   834 \N
85  f   2014-05-02  wittmer 0.086030    0   10  834 \N
86  f   2014-05-02  wittmer L026DH  0   14  834 \N
87  f   2014-05-02  wittmer 89.319970   0   2   835 \N
88  f   2014-05-02  wittmer 0893    0   4   835 \N
89  f   2014-05-02  wittmer 0.060000    0   9   835 \N
90  f   2014-05-02  wittmer L026DV  0   14  835 \N
91  f   2014-05-02  wittmer 89.359970   0   2   836 \N
92  f   2014-05-02  wittmer 0894    0   4   836 \N
93  f   2014-05-02  wittmer 0.035000    0   9   836 \N
94  f   2014-05-02  wittmer L027GV  0   14  836 \N
95  f   2014-05-02  wittmer 89.992495   0   2   837 \N
96  f   2014-05-02  wittmer 0900    0   4   837 \N
97  f   2014-05-02  wittmer 0.534856    0   9   837 \N
98  f   2014-05-02  wittmer 0.672525    0   10  837 \N
99  f   2014-05-02  wittmer L028DE  0   14  837 \N
100 f   2014-05-02  wittmer 90.500162   0   2   838 \N
101 f   2014-05-02  wittmer 0905    0   4   838 \N
102 f   2014-05-02  wittmer 0.150000    0   9   838 \N
103 f   2014-05-02  wittmer 0.507667    0   10  838 \N
104 f   2014-05-02  wittmer L030QS  0   14  838 \N
105 f   2014-05-02  wittmer 90.500162   0   2   839 \N
106 f   2014-05-02  wittmer 0905    0   4   839 \N
107 f   2014-05-02  wittmer 0.150000    0   9   839 \N
108 f   2014-05-02  wittmer L030DV  0   14  839 \N
109 f   2014-05-02  wittmer 91.009351   0   2   840 \N
110 f   2014-05-02  wittmer 0910    0   4   840 \N
111 f   2014-05-02  wittmer 0.534856    0   9   840 \N
112 f   2014-05-02  wittmer 0.509189    0   10  840 \N
113 f   2014-05-02  wittmer L032DE  0   14  840 \N
114 f   2014-05-02  wittmer 91.812274   0   2   841 \N
115 f   2014-05-02  wittmer 0918    0   4   841 \N
116 f   2014-05-02  wittmer 0.017831    0   10  841 \N
117 f   2014-05-02  wittmer L034FC  0   14  841 \N
118 f   2014-05-02  wittmer 91.794443   0   2   842 \N
119 f   2014-05-02  wittmer 0918    0   4   842 \N
120 f   2014-05-02  wittmer 0.000000    0   10  842 \N
121 f   2014-05-02  wittmer L034VC  0   14  842 \N
122 f   2014-05-02  wittmer 91.794443   0   2   843 \N
123 f   2014-05-02  wittmer 0918    0   4   843 \N
124 f   2014-05-02  wittmer 0.785092    0   10  843 \N
125 f   2014-05-02  wittmer L034VP  0   14  843 \N
126 f   2014-05-02  wittmer 91.755614   0   2   844 \N
127 f   2014-05-02  wittmer 0918    0   4   844 \N
128 f   2014-05-02  wittmer -0.038829   0   10  844 \N
129 f   2014-05-02  wittmer L034XC-R    0   14  844 \N
130 f   2014-05-02  wittmer 91.880473   0   2   845 \N
131 f   2014-05-02  wittmer 0919    0   4   845 \N
132 f   2014-05-02  wittmer 0.060000    0   9   845 \N
133 f   2014-05-02  wittmer 0.086030    0   10  845 \N
134 f   2014-05-02  wittmer L034DH  0   14  845 \N
135 f   2014-05-02  wittmer 91.880473   0   2   846 \N
136 f   2014-05-02  wittmer 0919    0   4   846 \N
137 f   2014-05-02  wittmer 0.060000    0   9   846 \N
138 f   2014-05-02  wittmer L034DV  0   14  846 \N
139 f   2014-05-02  wittmer 92.072622   0   2   847 \N
140 f   2014-05-02  wittmer 0921    0   4   847 \N
141 f   2014-05-02  wittmer 0.150000    0   9   847 \N
142 f   2014-05-02  wittmer 0.192149    0   10  847 \N
143 f   2014-05-02  wittmer L035QA  0   14  847 \N
144 f   2014-05-02  wittmer 92.252964   0   2   848 \N
145 f   2014-05-02  wittmer 0923    0   4   848 \N
146 f   2014-05-02  wittmer 0.150000    0   9   848 \N
147 f   2014-05-02  wittmer 0.180342    0   10  848 \N
148 f   2014-05-02  wittmer L035QB  0   14  848 \N
149 f   2014-05-02  wittmer 93.228680   0   2   849 \N
150 f   2014-05-02  wittmer 0932    0   4   849 \N
151 f   2014-05-02  wittmer 1.069419    0   9   849 \N
152 f   2014-05-02  wittmer 0.975716    0   10  849 \N
153 f   2014-05-02  wittmer L039DS  0   14  849 \N
154 f   2014-05-02  wittmer 94.230584   0   2   850 \N
155 f   2014-05-02  wittmer 0942    0   4   850 \N
156 f   2014-05-02  wittmer 0.150000    0   9   850 \N
157 f   2014-05-02  wittmer 1.001904    0   10  850 \N
158 f   2014-05-02  wittmer L042QA  0   14  850 \N
159 f   2014-05-02  wittmer 94.410926   0   2   851 \N
160 f   2014-05-02  wittmer 0944    0   4   851 \N
161 f   2014-05-02  wittmer 0.150000    0   9   851 \N
162 f   2014-05-02  wittmer 0.180342    0   10  851 \N
163 f   2014-05-02  wittmer L042QB  0   14  851 \N
164 f   2014-05-02  wittmer 94.694936   0   2   852 \N
165 f   2014-05-02  wittmer 0947    0   4   852 \N
166 f   2014-05-02  wittmer -0.024535   0   10  852 \N
167 f   2014-05-02  wittmer L044MCP 0   14  852 \N
168 f   2014-05-02  wittmer 94.719471   0   2   853 \N
169 f   2014-05-02  wittmer 0947    0   4   853 \N
170 f   2014-05-02  wittmer 0.000000    0   10  853 \N
171 f   2014-05-02  wittmer L044VC  0   14  853 \N
172 f   2014-05-02  wittmer 94.719471   0   2   854 \N
173 f   2014-05-02  wittmer 0947    0   4   854 \N
174 f   2014-05-02  wittmer 0.308545    0   10  854 \N
175 f   2014-05-02  wittmer L044VP  0   14  854 \N
176 f   2014-05-02  wittmer 94.737302   0   2   855 \N
177 f   2014-05-02  wittmer 0947    0   4   855 \N
178 f   2014-05-02  wittmer 0.017831    0   10  855 \N
179 f   2014-05-02  wittmer L044FC  0   14  855 \N
180 f   2014-05-02  wittmer 94.680642   0   2   856 \N
181 f   2014-05-02  wittmer 0947    0   4   856 \N
182 f   2014-05-02  wittmer -0.038829   0   10  856 \N
183 f   2014-05-02  wittmer L044XC-R    0   14  856 \N
184 f   2014-05-02  wittmer 94.804891   0   2   857 \N
185 f   2014-05-02  wittmer 0948    0   4   857 \N
186 f   2014-05-02  wittmer 0.060000    0   9   857 \N
187 f   2014-05-02  wittmer 0.085420    0   10  857 \N
188 f   2014-05-02  wittmer L044DH  0   14  857 \N
189 f   2014-05-02  wittmer 94.804891   0   2   858 \N
190 f   2014-05-02  wittmer 0948    0   4   858 \N
191 f   2014-05-02  wittmer 0.060000    0   9   858 \N
192 f   2014-05-02  wittmer L044DV  0   14  858 \N
193 f   2014-05-02  wittmer 94.893639   0   2   859 \N
194 f   2014-05-02  wittmer 0949    0   4   859 \N
195 f   2014-05-02  wittmer 0.035000    0   9   859 \N
196 f   2014-05-02  wittmer 0.088748    0   10  859 \N
197 f   2014-05-02  wittmer L045GV  0   14  859 \N
198 f   2014-05-02  wittmer 95.163996   0   2   860 \N
199 f   2014-05-02  wittmer 0952    0   4   860 \N
200 f   2014-05-02  wittmer 0.150000    0   9   860 \N
201 f   2014-05-02  wittmer 0.270357    0   10  860 \N
202 f   2014-05-02  wittmer L045QA  0   14  860 \N
203 f   2014-05-02  wittmer 95.344338   0   2   861 \N
204 f   2014-05-02  wittmer 0954    0   4   861 \N
205 f   2014-05-02  wittmer 0.150000    0   9   861 \N
206 f   2014-05-02  wittmer 0.180342    0   10  861 \N
207 f   2014-05-02  wittmer L045QB  0   14  861 \N
208 f   2014-05-02  wittmer 95.826047   0   2   862 \N
209 f   2014-05-02  wittmer 0958    0   4   862 \N
210 f   2014-05-02  wittmer 0.150000    0   9   862 \N
211 f   2014-05-02  wittmer 0.481709    0   10  862 \N
212 f   2014-05-02  wittmer L048QA  0   14  862 \N
213 f   2014-05-02  wittmer 96.006389   0   2   863 \N
214 f   2014-05-02  wittmer 0960    0   4   863 \N
215 f   2014-05-02  wittmer 0.150000    0   9   863 \N
216 f   2014-05-02  wittmer 0.180342    0   10  863 \N
217 f   2014-05-02  wittmer L048QB  0   14  863 \N
218 f   2014-05-02  wittmer 97.029025   0   2   864 \N
219 f   2014-05-02  wittmer 0970    0   4   864 \N
220 f   2014-05-02  wittmer 0.190000    0   9   864 \N
221 f   2014-05-02  wittmer 1.022636    0   10  864 \N
222 f   2014-05-02  wittmer L051DE  0   14  864 \N
223 f   2014-05-02  wittmer 97.439132   0   2   865 \N
224 f   2014-05-02  wittmer 0974    0   4   865 \N
225 f   2014-05-02  wittmer -0.025146   0   10  865 \N
226 f   2014-05-02  wittmer L052A   0   14  865 \N
227 f   2014-05-02  wittmer 97.482109   0   2   866 \N
228 f   2014-05-02  wittmer 0975    0   4   866 \N
229 f   2014-05-02  wittmer 0.017831    0   10  866 \N
230 f   2014-05-02  wittmer L052DC  0   14  866 \N
231 f   2014-05-02  wittmer 97.645253   0   2   867 \N
232 f   2014-05-02  wittmer 0976    0   4   867 \N
233 f   2014-05-02  wittmer 0.180975    0   10  867 \N
234 f   2014-05-02  wittmer L053VP  0   14  867 \N
235 f   2014-05-02  wittmer 97.645253   0   2   868 \N
236 f   2014-05-02  wittmer 0976    0   4   868 \N
237 f   2014-05-02  wittmer 0.000000    0   10  868 \N
238 f   2014-05-02  wittmer L053VC  0   14  868 \N
239 f   2014-05-02  wittmer 97.622378   0   2   869 \N
240 f   2014-05-02  wittmer 0976    0   4   869 \N
241 f   2014-05-02  wittmer -0.022875   0   10  869 \N
242 f   2014-05-02  wittmer L053MCP 0   14  869 \N
243 f   2014-05-02  wittmer 97.677663   0   2   870 \N
244 f   2014-05-02  wittmer 0977    0   4   870 \N
245 f   2014-05-02  wittmer 0.032410    0   10  870 \N
246 f   2014-05-02  wittmer L053FC  0   14  870 \N
247 f   2014-05-02  wittmer 97.869717   0   2   871 \N
248 f   2014-05-02  wittmer 0979    0   4   871 \N
249 f   2014-05-02  wittmer 0.150000    0   9   871 \N
250 f   2014-05-02  wittmer 0.224464    0   10  871 \N
251 f   2014-05-02  wittmer L054QA  0   14  871 \N
252 f   2014-05-02  wittmer 98.050059   0   2   872 \N
253 f   2014-05-02  wittmer 0981    0   4   872 \N
254 f   2014-05-02  wittmer 0.150000    0   9   872 \N
255 f   2014-05-02  wittmer 0.180342    0   10  872 \N
256 f   2014-05-02  wittmer L054QB  0   14  872 \N
257 f   2014-05-02  wittmer 98.531768   0   2   873 \N
258 f   2014-05-02  wittmer 0985    0   4   873 \N
259 f   2014-05-02  wittmer 0.150000    0   9   873 \N
260 f   2014-05-02  wittmer 0.481709    0   10  873 \N
261 f   2014-05-02  wittmer L057QA  0   14  873 \N
262 f   2014-05-02  wittmer 98.712110   0   2   874 \N
263 f   2014-05-02  wittmer 0987    0   4   874 \N
264 f   2014-05-02  wittmer 0.150000    0   9   874 \N
265 f   2014-05-02  wittmer 0.180342    0   10  874 \N
266 f   2014-05-02  wittmer L057QB  0   14  874 \N
267 f   2014-05-02  wittmer 98.972485   0   2   875 \N
268 f   2014-05-02  wittmer 0990    0   4   875 \N
269 f   2014-05-02  wittmer 0.260375    0   10  875 \N
270 f   2014-05-02  wittmer L058VP  0   14  875 \N
271 f   2014-05-02  wittmer 98.972485   0   2   876 \N
272 f   2014-05-02  wittmer 0990    0   4   876 \N
273 f   2014-05-02  wittmer 0.000000    0   10  876 \N
274 f   2014-05-02  wittmer L058VC  0   14  876 \N
275 f   2014-05-02  wittmer 99.348723   0   2   877 \N
276 f   2014-05-02  wittmer 0993    0   4   877 \N
277 f   2014-05-02  wittmer 0.200000    0   9   877 \N
278 f   2014-05-02  wittmer 0.376238    0   10  877 \N
279 f   2014-05-02  wittmer L059 MHB    0   14  877 \N
280 f   2014-05-02  wittmer 99.613918   0   2   878 \N
281 f   2014-05-02  wittmer 0996    0   4   878 \N
282 f   2014-05-02  wittmer 0.250000    0   9   878 \N
283 f   2014-05-02  wittmer 0.265195    0   10  878 \N
284 f   2014-05-02  wittmer L060SN  0   14  878 \N
285 f   2014-05-02  wittmer 99.819823   0   2   879 \N
286 f   2014-05-02  wittmer 0998    0   4   879 \N
287 f   2014-05-02  wittmer -0.035338   0   10  879 \N
288 f   2014-05-02  wittmer L061-R  0   14  879 \N
289 f   2014-05-02  wittmer 99.887571   0   2   880 \N
290 f   2014-05-02  wittmer 0999    0   4   880 \N
291 f   2014-05-02  wittmer 0.032410    0   10  880 \N
292 f   2014-05-02  wittmer L061FC  0   14  880 \N
293 f   2014-05-02  wittmer 99.855161   0   2   881 \N
294 f   2014-05-02  wittmer 0999    0   4   881 \N
295 f   2014-05-02  wittmer 0.241243    0   10  881 \N
296 f   2014-05-02  wittmer L061VP  0   14  881 \N
297 f   2014-05-02  wittmer 99.855161   0   2   882 \N
298 f   2014-05-02  wittmer 0999    0   4   882 \N
299 f   2014-05-02  wittmer 0.000000    0   10  882 \N
300 f   2014-05-02  wittmer L061VC  0   14  882 \N
301 f   2014-05-02  wittmer 99.855161   0   2   883 \N
302 f   2014-05-02  wittmer 0999    0   4   883 \N
303 f   2014-05-02  wittmer 0.000000    0   10  883 \N
304 f   2014-05-02  wittmer L061DC  0   14  883 \N
305 f   2014-05-02  wittmer 99.900000   0   2   884 \N
306 f   2014-05-02  wittmer 0999    0   4   884 \N
307 f   2014-05-02  wittmer 0.035000    0   9   884 \N
308 f   2014-05-02  wittmer L062GV  0   14  884 \N
309 f   2014-05-02  wittmer 101.735000  0   2   885 \N
310 f   2014-05-02  wittmer 1017    0   4   885 \N
311 f   2014-05-02  wittmer 3.470000    0   9   885 \N
312 f   2014-05-02  wittmer 1.879839    0   10  885 \N
313 f   2014-05-02  wittmer L062 RFQ    0   14  885 \N
314 f   2014-05-02  wittmer 103.540002  0   2   886 \N
315 f   2014-05-02  wittmer 1035    0   4   886 \N
316 f   2014-05-02  wittmer 0.035000    0   9   886 \N
317 f   2014-05-02  wittmer 1.805002    0   10  886 \N
318 f   2014-05-02  wittmer L071GV  0   14  886 \N
319 f   2014-05-02  wittmer 103.747787  0   2   887 \N
320 f   2014-05-02  wittmer 1037    0   4   887 \N
321 f   2014-05-02  wittmer 0.027051    0   10  887 \N
322 f   2014-05-02  wittmer L072XC-R    0   14  887 \N
323 f   2014-05-02  wittmer 103.695539  0   2   888 \N
324 f   2014-05-02  wittmer 1037    0   4   888 \N
325 f   2014-05-02  wittmer -0.025197   0   10  888 \N
326 f   2014-05-02  wittmer L072A   0   14  888 \N
327 f   2014-05-02  wittmer 103.755153  0   2   889 \N
328 f   2014-05-02  wittmer 1038    0   4   889 \N
329 f   2014-05-02  wittmer 0.034417    0   10  889 \N
330 f   2014-05-02  wittmer L072YC-R    0   14  889 \N
331 f   2014-05-02  wittmer 103.866398  0   2   890 \N
332 f   2014-05-02  wittmer 1039    0   4   890 \N
333 f   2014-05-02  wittmer -0.035338   0   10  890 \N
334 f   2014-05-02  wittmer L073-R  0   14  890 \N
335 f   2014-05-02  wittmer 103.901736  0   2   891 \N
336 f   2014-05-02  wittmer 1039    0   4   891 \N
337 f   2014-05-02  wittmer 0.181000    0   10  891 \N
338 f   2014-05-02  wittmer L073DC  0   14  891 \N
339 f   2014-05-02  wittmer 103.934146  0   2   892 \N
340 f   2014-05-02  wittmer 1039    0   4   892 \N
341 f   2014-05-02  wittmer 0.032410    0   10  892 \N
342 f   2014-05-02  wittmer L073FC  0   14  892 \N
343 f   2014-05-02  wittmer 104.160596  0   2   893 \N
344 f   2014-05-02  wittmer 1042    0   4   893 \N
345 f   2014-05-02  wittmer 0.035000    0   9   893 \N
346 f   2014-05-02  wittmer 0.258860    0   10  893 \N
347 f   2014-05-02  wittmer L073GV  0   14  893 \N
348 f   2014-05-02  wittmer 104.509143  0   2   894 \N
349 f   2014-05-02  wittmer 1045    0   4   894 \N
350 f   2014-05-02  wittmer 0.200000    0   9   894 \N
351 f   2014-05-02  wittmer L076DH  0   14  894 \N
352 f   2014-05-02  wittmer 104.509143  0   2   895 \N
353 f   2014-05-02  wittmer 1045    0   4   895 \N
354 f   2014-05-02  wittmer 0.200000    0   9   895 \N
355 f   2014-05-02  wittmer L076DV  0   14  895 \N
356 f   2014-05-02  wittmer 104.509143  0   2   896 \N
357 f   2014-05-02  wittmer 1045    0   4   896 \N
358 f   2014-05-02  wittmer 0.200000    0   9   896 \N
359 f   2014-05-02  wittmer 0.348547    0   10  896 \N
360 f   2014-05-02  wittmer L076SN  0   14  896 \N
361 f   2014-05-02  wittmer 104.852143  0   2   897 \N
362 f   2014-05-02  wittmer 1049    0   4   897 \N
363 f   2014-05-02  wittmer 0.240000    0   9   897 \N
364 f   2014-05-02  wittmer 0.343000    0   10  897 \N
365 f   2014-05-02  wittmer L077    0   14  897 \N
366 f   2014-05-02  wittmer 105.195143  0   2   898 \N
367 f   2014-05-02  wittmer 1052    0   4   898 \N
368 f   2014-05-02  wittmer 0.200000    0   9   898 \N
369 f   2014-05-02  wittmer L078DH  0   14  898 \N
370 f   2014-05-02  wittmer 105.195143  0   2   899 \N
371 f   2014-05-02  wittmer 1052    0   4   899 \N
372 f   2014-05-02  wittmer 0.200000    0   9   899 \N
373 f   2014-05-02  wittmer L078DV  0   14  899 \N
374 f   2014-05-02  wittmer 105.195143  0   2   900 \N
375 f   2014-05-02  wittmer 1052    0   4   900 \N
376 f   2014-05-02  wittmer 0.200000    0   9   900 \N
377 f   2014-05-02  wittmer 0.343000    0   10  900 \N
378 f   2014-05-02  wittmer L078SN  0   14  900 \N
379 f   2014-05-02  wittmer 105.543778  0   2   901 \N
380 f   2014-05-02  wittmer 1055    0   4   901 \N
381 f   2014-05-02  wittmer 0.035000    0   9   901 \N
382 f   2014-05-02  wittmer 0.348635    0   10  901 \N
383 f   2014-05-02  wittmer L079GV  0   14  901 \N
384 f   2014-05-02  wittmer 105.833592  0   2   902 \N
385 f   2014-05-02  wittmer 1058    0   4   902 \N
386 f   2014-05-02  wittmer -0.035338   0   10  902 \N
387 f   2014-05-02  wittmer L080-R  0   14  902 \N
388 f   2014-05-02  wittmer 105.833984  0   2   903 \N
389 f   2014-05-02  wittmer 1058    0   4   903 \N
390 f   2014-05-02  wittmer -0.034946   0   10  903 \N
391 f   2014-05-02  wittmer L080FS  0   14  903 \N
392 f   2014-05-02  wittmer 105.851805  0   2   904 \N
393 f   2014-05-02  wittmer 1059    0   4   904 \N
394 f   2014-05-02  wittmer -0.017125   0   10  904 \N
395 f   2014-05-02  wittmer L080A   0   14  904 \N
396 f   2014-05-02  wittmer 105.868930  0   2   905 \N
397 f   2014-05-02  wittmer 1059    0   4   905 \N
398 f   2014-05-02  wittmer 0.325152    0   10  905 \N
399 f   2014-05-02  wittmer L080DC  0   14  905 \N
400 f   2014-05-02  wittmer 105.901340  0   2   906 \N
401 f   2014-05-02  wittmer 1059    0   4   906 \N
402 f   2014-05-02  wittmer 0.032410    0   10  906 \N
403 f   2014-05-02  wittmer L080FC  0   14  906 \N
404 f   2014-05-02  wittmer 106.230171  0   2   907 \N
405 f   2014-05-02  wittmer 1062    0   4   907 \N
406 f   2014-05-02  wittmer 0.035000    0   9   907 \N
407 f   2014-05-02  wittmer 0.361241    0   10  907 \N
408 f   2014-05-02  wittmer L080GV  0   14  907 \N
409 f   2014-05-02  wittmer 106.518698  0   2   908 \N
410 f   2014-05-02  wittmer 1065    0   4   908 \N
411 f   2014-05-02  wittmer 0.240000    0   9   908 \N
412 f   2014-05-02  wittmer 0.288527    0   10  908 \N
413 f   2014-05-02  wittmer L082    0   14  908 \N
414 f   2014-05-02  wittmer 106.861698  0   2   909 \N
415 f   2014-05-02  wittmer 1069    0   4   909 \N
416 f   2014-05-02  wittmer 0.200000    0   9   909 \N
417 f   2014-05-02  wittmer L083DH  0   14  909 \N
418 f   2014-05-02  wittmer 106.861698  0   2   910 \N
419 f   2014-05-02  wittmer 1069    0   4   910 \N
420 f   2014-05-02  wittmer 0.200000    0   9   910 \N
421 f   2014-05-02  wittmer L083DV  0   14  910 \N
422 f   2014-05-02  wittmer 106.861698  0   2   911 \N
423 f   2014-05-02  wittmer 1069    0   4   911 \N
424 f   2014-05-02  wittmer 0.200000    0   9   911 \N
425 f   2014-05-02  wittmer 0.343000    0   10  911 \N
426 f   2014-05-02  wittmer L083SN  0   14  911 \N
427 f   2014-05-02  wittmer 107.204698  0   2   912 \N
428 f   2014-05-02  wittmer 1072    0   4   912 \N
429 f   2014-05-02  wittmer 0.240000    0   9   912 \N
430 f   2014-05-02  wittmer 0.343000    0   10  912 \N
431 f   2014-05-02  wittmer L084    0   14  912 \N
432 f   2014-05-02  wittmer 107.479698  0   2   913 \N
433 f   2014-05-02  wittmer 1075    0   4   913 \N
434 f   2014-05-02  wittmer 0.240000    0   9   913 \N
435 f   2014-05-02  wittmer 0.275000    0   10  913 \N
436 f   2014-05-02  wittmer L085    0   14  913 \N
437 f   2014-05-02  wittmer 107.822698  0   2   914 \N
438 f   2014-05-02  wittmer 1078    0   4   914 \N
439 f   2014-05-02  wittmer 0.200000    0   9   914 \N
440 f   2014-05-02  wittmer L087DH  0   14  914 \N
441 f   2014-05-02  wittmer 107.822698  0   2   915 \N
442 f   2014-05-02  wittmer 1078    0   4   915 \N
443 f   2014-05-02  wittmer 0.200000    0   9   915 \N
444 f   2014-05-02  wittmer L087DV  0   14  915 \N
445 f   2014-05-02  wittmer 107.822698  0   2   916 \N
446 f   2014-05-02  wittmer 1078    0   4   916 \N
447 f   2014-05-02  wittmer 0.200000    0   9   916 \N
448 f   2014-05-02  wittmer 0.343000    0   10  916 \N
449 f   2014-05-02  wittmer L087SN  0   14  916 \N
450 f   2014-05-02  wittmer 108.165698  0   2   917 \N
451 f   2014-05-02  wittmer 1082    0   4   917 \N
452 f   2014-05-02  wittmer 0.240000    0   9   917 \N
453 f   2014-05-02  wittmer 0.343000    0   10  917 \N
454 f   2014-05-02  wittmer L088    0   14  917 \N
455 f   2014-05-02  wittmer 108.440698  0   2   918 \N
456 f   2014-05-02  wittmer 1084    0   4   918 \N
457 f   2014-05-02  wittmer 0.240000    0   9   918 \N
458 f   2014-05-02  wittmer 0.275000    0   10  918 \N
459 f   2014-05-02  wittmer L089    0   14  918 \N
460 f   2014-05-02  wittmer 108.783698  0   2   919 \N
461 f   2014-05-02  wittmer 1088    0   4   919 \N
462 f   2014-05-02  wittmer 0.200000    0   9   919 \N
463 f   2014-05-02  wittmer L090DH  0   14  919 \N
464 f   2014-05-02  wittmer 108.783698  0   2   920 \N
465 f   2014-05-02  wittmer 1088    0   4   920 \N
466 f   2014-05-02  wittmer 0.200000    0   9   920 \N
467 f   2014-05-02  wittmer L090DV  0   14  920 \N
468 f   2014-05-02  wittmer 108.783698  0   2   921 \N
469 f   2014-05-02  wittmer 1088    0   4   921 \N
470 f   2014-05-02  wittmer 0.200000    0   9   921 \N
471 f   2014-05-02  wittmer 0.343000    0   10  921 \N
472 f   2014-05-02  wittmer L090SN  0   14  921 \N
473 f   2014-05-02  wittmer 109.126698  0   2   922 \N
474 f   2014-05-02  wittmer 1091    0   4   922 \N
475 f   2014-05-02  wittmer 0.240000    0   9   922 \N
476 f   2014-05-02  wittmer 0.343000    0   10  922 \N
477 f   2014-05-02  wittmer L091    0   14  922 \N
478 f   2014-05-02  wittmer 109.415923  0   2   923 \N
479 f   2014-05-02  wittmer 1094    0   4   923 \N
480 f   2014-05-02  wittmer 0.035000    0   9   923 \N
481 f   2014-05-02  wittmer 0.289225    0   10  923 \N
482 f   2014-05-02  wittmer L092GV  0   14  923 \N
483 f   2014-05-02  wittmer 109.619716  0   2   924 \N
484 f   2014-05-02  wittmer 1096    0   4   924 \N
485 f   2014-05-02  wittmer -0.035338   0   10  924 \N
486 f   2014-05-02  wittmer L092-R  0   14  924 \N
487 f   2014-05-02  wittmer 109.620108  0   2   925 \N
488 f   2014-05-02  wittmer 1096    0   4   925 \N
489 f   2014-05-02  wittmer -0.034946   0   10  925 \N
490 f   2014-05-02  wittmer L092FS  0   14  925 \N
491 f   2014-05-02  wittmer 109.655054  0   2   926 \N
492 f   2014-05-02  wittmer 1097    0   4   926 \N
493 f   2014-05-02  wittmer 0.239132    0   10  926 \N
494 f   2014-05-02  wittmer L092DC  0   14  926 \N
495 f   2014-05-02  wittmer 109.687464  0   2   927 \N
496 f   2014-05-02  wittmer 1097    0   4   927 \N
497 f   2014-05-02  wittmer 0.032410    0   10  927 \N
498 f   2014-05-02  wittmer L092FC  0   14  927 \N
499 f   2014-05-02  wittmer 109.874908  0   2   928 \N
500 f   2014-05-02  wittmer 1099    0   4   928 \N
501 f   2014-05-02  wittmer 0.035000    0   9   928 \N
502 f   2014-05-02  wittmer 0.219854    0   10  928 \N
503 f   2014-05-02  wittmer L093GV  0   14  928 \N
504 f   2014-05-02  wittmer 110.245345  0   2   929 \N
505 f   2014-05-02  wittmer 1102    0   4   929 \N
506 f   2014-05-02  wittmer 0.300000    0   9   929 \N
507 f   2014-05-02  wittmer 0.370438    0   10  929 \N
508 f   2014-05-02  wittmer L094    0   14  929 \N
509 f   2014-05-02  wittmer 110.649645  0   2   930 \N
510 f   2014-05-02  wittmer 1106    0   4   930 \N
511 f   2014-05-02  wittmer 0.200000    0   9   930 \N
512 f   2014-05-02  wittmer L096DH  0   14  930 \N
513 f   2014-05-02  wittmer 110.649645  0   2   931 \N
514 f   2014-05-02  wittmer 1106    0   4   931 \N
515 f   2014-05-02  wittmer 0.200000    0   9   931 \N
516 f   2014-05-02  wittmer L096DV  0   14  931 \N
517 f   2014-05-02  wittmer 110.649645  0   2   932 \N
518 f   2014-05-02  wittmer 1106    0   4   932 \N
519 f   2014-05-02  wittmer 0.200000    0   9   932 \N
520 f   2014-05-02  wittmer 0.404300    0   10  932 \N
521 f   2014-05-02  wittmer L096SN  0   14  932 \N
522 f   2014-05-02  wittmer 111.053945  0   2   933 \N
523 f   2014-05-02  wittmer 1111    0   4   933 \N
524 f   2014-05-02  wittmer 0.300000    0   9   933 \N
525 f   2014-05-02  wittmer 0.404300    0   10  933 \N
526 f   2014-05-02  wittmer L097    0   14  933 \N
527 f   2014-05-02  wittmer 111.451545  0   2   934 \N
528 f   2014-05-02  wittmer 1115    0   4   934 \N
529 f   2014-05-02  wittmer 0.300000    0   9   934 \N
530 f   2014-05-02  wittmer 0.397600    0   10  934 \N
531 f   2014-05-02  wittmer L098    0   14  934 \N
532 f   2014-05-02  wittmer 111.849345  0   2   935 \N
533 f   2014-05-02  wittmer 1118    0   4   935 \N
534 f   2014-05-02  wittmer 0.300000    0   9   935 \N
535 f   2014-05-02  wittmer 0.397800    0   10  935 \N
536 f   2014-05-02  wittmer L100    0   14  935 \N
537 f   2014-05-02  wittmer 112.253645  0   2   936 \N
538 f   2014-05-02  wittmer 1123    0   4   936 \N
539 f   2014-05-02  wittmer 0.200000    0   9   936 \N
540 f   2014-05-02  wittmer L101DH  0   14  936 \N
541 f   2014-05-02  wittmer 112.253645  0   2   937 \N
542 f   2014-05-02  wittmer 1123    0   4   937 \N
543 f   2014-05-02  wittmer 0.200000    0   9   937 \N
544 f   2014-05-02  wittmer L101DV  0   14  937 \N
545 f   2014-05-02  wittmer 112.253645  0   2   938 \N
546 f   2014-05-02  wittmer 1123    0   4   938 \N
547 f   2014-05-02  wittmer 0.200000    0   9   938 \N
548 f   2014-05-02  wittmer 0.404300    0   10  938 \N
549 f   2014-05-02  wittmer L101SN  0   14  938 \N
550 f   2014-05-02  wittmer 112.657945  0   2   939 \N
551 f   2014-05-02  wittmer 1127    0   4   939 \N
552 f   2014-05-02  wittmer 0.300000    0   9   939 \N
553 f   2014-05-02  wittmer 0.404300    0   10  939 \N
554 f   2014-05-02  wittmer L102    0   14  939 \N
555 f   2014-05-02  wittmer 113.055645  0   2   940 \N
556 f   2014-05-02  wittmer 1131    0   4   940 \N
557 f   2014-05-02  wittmer 0.300000    0   9   940 \N
558 f   2014-05-02  wittmer 0.397700    0   10  940 \N
559 f   2014-05-02  wittmer L104    0   14  940 \N
560 f   2014-05-02  wittmer 113.453345  0   2   941 \N
561 f   2014-05-02  wittmer 1135    0   4   941 \N
562 f   2014-05-02  wittmer 0.300000    0   9   941 \N
563 f   2014-05-02  wittmer 0.397700    0   10  941 \N
564 f   2014-05-02  wittmer L105    0   14  941 \N
565 f   2014-05-02  wittmer 113.857645  0   2   942 \N
566 f   2014-05-02  wittmer 1139    0   4   942 \N
567 f   2014-05-02  wittmer 0.200000    0   9   942 \N
568 f   2014-05-02  wittmer L106DH  0   14  942 \N
569 f   2014-05-02  wittmer 113.857645  0   2   943 \N
570 f   2014-05-02  wittmer 1139    0   4   943 \N
571 f   2014-05-02  wittmer 0.200000    0   9   943 \N
572 f   2014-05-02  wittmer L106DV  0   14  943 \N
573 f   2014-05-02  wittmer 113.857645  0   2   944 \N
574 f   2014-05-02  wittmer 1139    0   4   944 \N
575 f   2014-05-02  wittmer 0.200000    0   9   944 \N
576 f   2014-05-02  wittmer 0.404300    0   10  944 \N
577 f   2014-05-02  wittmer L106SN  0   14  944 \N
578 f   2014-05-02  wittmer 114.261945  0   2   945 \N
579 f   2014-05-02  wittmer 1143    0   4   945 \N
580 f   2014-05-02  wittmer 0.300000    0   9   945 \N
581 f   2014-05-02  wittmer 0.404300    0   10  945 \N
582 f   2014-05-02  wittmer L108    0   14  945 \N
583 f   2014-05-02  wittmer 114.605637  0   2   946 \N
584 f   2014-05-02  wittmer 1146    0   4   946 \N
585 f   2014-05-02  wittmer 0.035000    0   9   946 \N
586 f   2014-05-02  wittmer 0.343692    0   10  946 \N
587 f   2014-05-02  wittmer L108GV  0   14  946 \N
588 f   2014-05-02  wittmer 114.847163  0   2   947 \N
589 f   2014-05-02  wittmer 1148    0   4   947 \N
590 f   2014-05-02  wittmer -0.035338   0   10  947 \N
591 f   2014-05-02  wittmer L110-R  0   14  947 \N
592 f   2014-05-02  wittmer 114.847555  0   2   948 \N
593 f   2014-05-02  wittmer 1148    0   4   948 \N
594 f   2014-05-02  wittmer -0.034946   0   10  948 \N
595 f   2014-05-02  wittmer L110FS  0   14  948 \N
596 f   2014-05-02  wittmer 114.882501  0   2   949 \N
597 f   2014-05-02  wittmer 1149    0   4   949 \N
598 f   2014-05-02  wittmer 0.276864    0   10  949 \N
599 f   2014-05-02  wittmer L110DC  0   14  949 \N
600 f   2014-05-02  wittmer 114.914911  0   2   950 \N
601 f   2014-05-02  wittmer 1149    0   4   950 \N
602 f   2014-05-02  wittmer 0.032410    0   10  950 \N
603 f   2014-05-02  wittmer L110FC  0   14  950 \N
604 f   2014-05-02  wittmer 115.552380  0   2   951 \N
605 f   2014-05-02  wittmer 1156    0   4   951 \N
606 f   2014-05-02  wittmer 0.750000    0   9   951 \N
607 f   2014-05-02  wittmer 0.669879    0   10  951 \N
608 f   2014-05-02  wittmer L   0   14  951 \N
609 f   2014-05-02  wittmer 116.076980  0   2   952 \N
610 f   2014-05-02  wittmer 1161    0   4   952 \N
611 f   2014-05-02  wittmer 0.035000    0   9   952 \N
612 f   2014-05-02  wittmer 0.524600    0   10  952 \N
613 f   2014-05-02  wittmer L   0   14  952 \N
614 f   2014-05-02  wittmer 116.315283  0   2   953 \N
615 f   2014-05-02  wittmer 1163    0   4   953 \N
616 f   2014-05-02  wittmer -0.038285   0   10  953 \N
617 f   2014-05-02  wittmer L   0   14  953 \N
618 f   2014-05-02  wittmer 116.385978  0   2   954 \N
619 f   2014-05-02  wittmer 1164    0   4   954 \N
620 f   2014-05-02  wittmer 0.032410    0   10  954 \N
621 f   2014-05-02  wittmer L   0   14  954 \N
622 f   2014-05-02  wittmer 116.376380  0   2   955 \N
623 f   2014-05-02  wittmer 1164    0   4   955 \N
624 f   2014-05-02  wittmer 0.150000    0   9   955 \N
625 f   2014-05-02  wittmer 0.299400    0   10  955 \N
626 f   2014-05-02  wittmer L   0   14  955 \N
627 f   2014-05-02  wittmer 116.582551  0   2   956 \N
628 f   2014-05-02  wittmer 1166    0   4   956 \N
629 f   2014-05-02  wittmer -0.038829   0   10  956 \N
630 f   2014-05-02  wittmer L   0   14  956 \N
631 f   2014-05-02  wittmer 1166    0   4   957 \N
632 f   2014-05-02  wittmer 1166    0   4   958 \N
633 f   2014-05-02  wittmer 116.926380  0   2   959 \N
634 f   2014-05-02  wittmer 1169    0   4   959 \N
635 f   2014-05-02  wittmer 0.150000    0   9   959 \N
636 f   2014-05-02  wittmer 0.305000    0   10  959 \N
637 f   2014-05-02  wittmer L   0   14  959 \N
638 f   2014-05-02  wittmer 117.195965  0   2   960 \N
639 f   2014-05-02  wittmer 1172    0   4   960 \N
640 f   2014-05-02  wittmer 0.080010    0   9   960 \N
641 f   2014-05-02  wittmer 0.269585    0   10  960 \N
642 f   2014-05-02  wittmer L   0   14  960 \N
643 f   2014-05-02  wittmer 117.195965  0   2   961 \N
644 f   2014-05-02  wittmer 1172    0   4   961 \N
645 f   2014-05-02  wittmer 0.080010    0   9   961 \N
646 f   2014-05-02  wittmer L   0   14  961 \N
647 f   2014-05-02  wittmer 117.401380  0   2   962 \N
648 f   2014-05-02  wittmer 1174    0   4   962 \N
649 f   2014-05-02  wittmer 0.150000    0   9   962 \N
650 f   2014-05-02  wittmer 0.205415    0   10  962 \N
651 f   2014-05-02  wittmer L   0   14  962 \N
652 f   2014-05-02  wittmer 117.776380  0   2   963 \N
653 f   2014-05-02  wittmer 1178    0   4   963 \N
654 f   2014-05-02  wittmer 0.375000    0   10  963 \N
655 f   2014-05-02  wittmer L   0   14  963 \N
656 f   2014-05-02  wittmer 117.776380  0   2   964 \N
657 f   2014-05-02  wittmer 1178    0   4   964 \N
658 f   2014-05-02  wittmer 0.000000    0   10  964 \N
659 f   2014-05-02  wittmer L   0   14  964 \N
660 f   2014-05-02  wittmer 117.808790  0   2   965 \N
661 f   2014-05-02  wittmer 1178    0   4   965 \N
662 f   2014-05-02  wittmer 0.032410    0   10  965 \N
663 f   2014-05-02  wittmer L   0   14  965 \N
664 f   2014-05-02  wittmer 1178    0   4   966 \N
665 f   2014-05-02  wittmer L   0   14  966 \N
666 f   2014-05-02  wittmer 118.151380  0   2   967 \N
667 f   2014-05-02  wittmer 1182    0   4   967 \N
668 f   2014-05-02  wittmer 0.150000    0   9   967 \N
669 f   2014-05-02  wittmer 0.375000    0   10  967 \N
670 f   2014-05-02  wittmer L   0   14  967 \N
671 f   2014-05-02  wittmer 118.626380  0   2   968 \N
672 f   2014-05-02  wittmer 1186    0   4   968 \N
673 f   2014-05-02  wittmer 0.150000    0   9   968 \N
674 f   2014-05-02  wittmer 0.475000    0   10  968 \N
675 f   2014-05-02  wittmer L   0   14  968 \N
676 f   2014-05-02  wittmer 119.176380  0   2   969 \N
677 f   2014-05-02  wittmer 1192    0   4   969 \N
678 f   2014-05-02  wittmer 0.150000    0   9   969 \N
679 f   2014-05-02  wittmer 0.550000    0   10  969 \N
680 f   2014-05-02  wittmer L   0   14  969 \N
681 f   2014-05-02  wittmer 120.000380  0   2   970 \N
682 f   2014-05-02  wittmer 1200    0   4   970 \N
683 f   2014-05-02  wittmer 0.750000    0   9   970 \N
684 f   2014-05-02  wittmer 0.824000    0   10  970 \N
685 f   2014-05-02  wittmer L   0   14  970 \N
686 f   2014-05-02  wittmer 120.524109  0   2   971 \N
687 f   2014-05-02  wittmer 1205    0   4   971 \N
688 f   2014-05-02  wittmer 0.035000    0   9   971 \N
689 f   2014-05-02  wittmer 0.523729    0   10  971 \N
690 f   2014-05-02  wittmer L   0   14  971 \N
691 f   2014-05-02  wittmer 1207    0   4   972 \N
692 f   2014-05-02  wittmer 0.220066    0   10  972 \N
693 f   2014-05-02  wittmer L   0   14  972 \N
694 f   2014-05-02  wittmer 120.708836  0   2   973 \N
695 f   2014-05-02  wittmer 1207    0   4   973 \N
696 f   2014-05-02  wittmer -0.035338   0   10  973 \N
697 f   2014-05-02  wittmer L   0   14  973 \N
698 f   2014-05-02  wittmer 120.709228  0   2   974 \N
699 f   2014-05-02  wittmer 1207    0   4   974 \N
700 f   2014-05-02  wittmer -0.034946   0   10  974 \N
701 f   2014-05-02  wittmer L   0   14  974 \N
702 f   2014-05-02  wittmer 120.776584  0   2   975 \N
703 f   2014-05-02  wittmer 1207    0   4   975 \N
704 f   2014-05-02  wittmer 0.032410    0   10  975 \N
705 f   2014-05-02  wittmer L   0   14  975 \N
706 f   2014-05-02  wittmer 1207    0   4   976 \N
707 f   2014-05-02  wittmer L   0   14  976 \N
708 f   2014-05-02  wittmer 121.157153  0   2   977 \N
709 f   2014-05-02  wittmer 1212    0   4   977 \N
710 f   2014-05-02  wittmer 0.412979    0   10  977 \N
711 f   2014-05-02  wittmer L   0   14  977 \N
712 f   2014-05-02  wittmer 121.919083  0   2   978 \N
713 f   2014-05-02  wittmer 1219    0   4   978 \N
714 f   2014-05-02  wittmer 0.100000    0   9   978 \N
715 f   2014-05-02  wittmer 0.269567    0   10  978 \N
716 f   2014-05-02  wittmer 1219    0   4   979 \N
717 f   2014-05-02  wittmer 0.100000    0   9   979 \N
718 f   2014-05-02  wittmer 122.124083  0   2   980 \N
719 f   2014-05-02  wittmer 1221    0   4   980 \N
720 f   2014-05-02  wittmer 0.150000    0   9   980 \N
721 f   2014-05-02  wittmer 0.205337    0   10  980 \N
722 f   2014-05-02  wittmer 122.774077  0   2   981 \N
723 f   2014-05-02  wittmer 1228    0   4   981 \N
724 f   2014-05-02  wittmer 0.150000    0   9   981 \N
725 f   2014-05-02  wittmer 0.205337    0   10  981 \N
726 f   2014-05-02  wittmer 124.530078  0   2   982 \N
727 f   2014-05-02  wittmer 1245    0   4   982 \N
728 f   2014-05-02  wittmer 0.150000    0   9   982 \N
729 f   2014-05-02  wittmer 0.205337    0   10  982 \N
730 f   2014-05-02  wittmer 125.180083  0   2   983 \N
731 f   2014-05-02  wittmer 1252    0   4   983 \N
732 f   2014-05-02  wittmer 0.150000    0   9   983 \N
733 f   2014-05-02  wittmer 0.205337    0   10  983 \N
734 f   2014-05-02  wittmer 125.611358  0   2   984 \N
735 f   2014-05-02  wittmer 1256    0   4   984 \N
736 f   2014-05-02  wittmer 0.374571    0   10  984 \N
737 f   2014-05-02  wittmer 1256    0   4   985 \N
738 f   2014-05-02  wittmer 0.000000    0   10  985 \N
739 f   2014-05-02  wittmer 1256    0   4   986 \N
740 f   2014-05-02  wittmer -0.035338   0   10  986 \N
741 f   2014-05-02  wittmer 1256    0   4   987 \N
742 f   2014-05-02  wittmer -0.035338   0   10  987 \N
743 f   2014-05-02  wittmer 1256    0   4   988 \N
744 f   2014-05-02  wittmer -0.035338   0   10  988 \N
745 f   2014-05-02  wittmer 1256    0   4   989 \N
746 f   2014-05-02  wittmer -0.035338   0   10  989 \N
747 f   2014-05-02  wittmer 1256    0   4   990 \N
748 f   2014-05-02  wittmer 0.412979    0   10  990 \N
749 f   2014-05-02  wittmer 125.827260  0   2   991 \N
750 f   2014-05-02  wittmer 1256    0   4   991 \N
751 f   2014-05-02  wittmer 1256    0   4   992 \N
752 f   2014-05-02  wittmer 1256    0   4   993 \N
753 f   2014-05-02  wittmer 127.020082  0   2   994 \N
754 f   2014-05-02  wittmer 1270    0   4   994 \N
755 f   2014-05-02  wittmer 0.100000    0   9   994 \N
756 f   2014-05-02  wittmer 0.269567    0   10  994 \N
757 f   2014-05-02  wittmer 1270    0   4   995 \N
758 f   2014-05-02  wittmer 0.100000    0   9   995 \N
759 f   2014-05-02  wittmer 127.225082  0   2   996 \N
760 f   2014-05-02  wittmer 1272    0   4   996 \N
761 f   2014-05-02  wittmer 0.150000    0   9   996 \N
762 f   2014-05-02  wittmer 0.205337    0   10  996 \N
763 f   2014-05-02  wittmer 127.560082  0   2   997 \N
764 f   2014-05-02  wittmer 1275    0   4   997 \N
765 f   2014-05-02  wittmer 0.150000    0   9   997 \N
766 f   2014-05-02  wittmer 0.205337    0   10  997 \N
767 f   2014-05-02  wittmer 128.175080  0   2   998 \N
768 f   2014-05-02  wittmer 1281    0   4   998 \N
769 f   2014-05-02  wittmer 0.150000    0   9   998 \N
770 f   2014-05-02  wittmer 0.205337    0   10  998 \N
771 f   2014-05-02  wittmer 128.510083  0   2   999 \N
772 f   2014-05-02  wittmer 1285    0   4   999 \N
773 f   2014-05-02  wittmer 0.150000    0   9   999 \N
774 f   2014-05-02  wittmer 0.205337    0   10  999 \N
775 f   2014-05-02  wittmer 1288    0   4   1000    \N
776 f   2014-05-02  wittmer 129.568602  0   2   1001    \N
777 f   2014-05-02  wittmer 1296    0   4   1001    \N
778 f   2014-05-02  wittmer 0.750000    0   9   1001    \N
779 f   2014-05-02  wittmer 0.678197    0   10  1001    \N
780 f   2014-05-02  wittmer 1301    0   4   1002    \N
781 f   2014-05-02  wittmer 0.035000    0   9   1002    \N
782 f   2014-05-02  wittmer 0.523729    0   10  1002    \N
783 f   2014-05-02  wittmer 1301    0   4   1003    \N
784 f   2014-05-02  wittmer 130.418612  0   2   1004    \N
785 f   2014-05-02  wittmer 1305    0   4   1004    \N
786 f   2014-05-02  wittmer 0.100000    0   9   1004    \N
787 f   2014-05-02  wittmer 0.269567    0   10  1004    \N
788 f   2014-05-02  wittmer 1305    0   4   1005    \N
789 f   2014-05-02  wittmer 0.100000    0   9   1005    \N
790 f   2014-05-02  wittmer 130.708612  0   2   1006    \N
791 f   2014-05-02  wittmer 1307    0   4   1006    \N
792 f   2014-05-02  wittmer 0.150000    0   9   1006    \N
793 f   2014-05-02  wittmer 0.205337    0   10  1006    \N
794 f   2014-05-02  wittmer 131.078612  0   2   1007    \N
795 f   2014-05-02  wittmer 1310    0   4   1007    \N
796 f   2014-05-02  wittmer 0.150000    0   9   1007    \N
797 f   2014-05-02  wittmer 0.205337    0   10  1007    \N
798 f   2014-05-02  wittmer 131.477712  0   2   1008    \N
799 f   2014-05-02  wittmer 1316    0   4   1008    \N
800 f   2014-05-02  wittmer -0.038829   0   10  1008    \N
801 f   2014-05-02  wittmer 1316    0   4   1009    \N
802 f   2014-05-02  wittmer 1316    0   4   1010    \N
803 f   2014-05-02  wittmer 0.374571    0   10  1010    \N
804 f   2014-05-02  wittmer 1316    0   4   1011    \N
805 f   2014-05-02  wittmer 0.000000    0   10  1011    \N
806 f   2014-05-02  wittmer 131.693612  0   2   1012    \N
807 f   2014-05-02  wittmer 1316    0   4   1012    \N
808 f   2014-05-02  wittmer 0.412979    0   10  1012    \N
809 f   2014-05-02  wittmer 1316    0   4   1013    \N
810 f   2014-05-02  wittmer 1316    0   4   1014    \N
811 f   2014-05-02  wittmer 132.308612  0   2   1015    \N
812 f   2014-05-02  wittmer 1323    0   4   1015    \N
813 f   2014-05-02  wittmer 0.150000    0   9   1015    \N
814 f   2014-05-02  wittmer 0.205337    0   10  1015    \N
815 f   2014-05-02  wittmer 132.678612  0   2   1016    \N
816 f   2014-05-02  wittmer 1327    0   4   1016    \N
817 f   2014-05-02  wittmer 0.150000    0   9   1016    \N
818 f   2014-05-02  wittmer 0.205337    0   10  1016    \N
819 f   2014-05-02  wittmer 133.818622  0   2   1017    \N
820 f   2014-05-02  wittmer 1338    0   4   1017    \N
821 f   2014-05-02  wittmer 0.750000    0   9   1017    \N
822 f   2014-05-02  wittmer 0.678197    0   10  1017    \N
823 f   2014-05-02  wittmer 134.523632  0   2   1018    \N
824 f   2014-05-02  wittmer 1345    0   4   1018    \N
825 f   2014-05-02  wittmer 0.100000    0   9   1018    \N
826 f   2014-05-02  wittmer 1345    0   4   1019    \N
827 f   2014-05-02  wittmer 134.813632  0   2   1020    \N
828 f   2014-05-02  wittmer 1346    0   4   1020    \N
829 f   2014-05-02  wittmer 0.150000    0   9   1020    \N
830 f   2014-05-02  wittmer 0.205337    0   10  1020    \N
831 f   2014-05-02  wittmer 135.153632  0   2   1021    \N
832 f   2014-05-02  wittmer 1351    0   4   1021    \N
833 f   2014-05-02  wittmer 0.150000    0   9   1021    \N
834 f   2014-05-02  wittmer 0.205337    0   10  1021    \N
835 f   2014-05-02  wittmer 1355    0   4   1022    \N
836 f   2014-05-02  wittmer 0.032410    0   10  1022    \N
837 f   2014-05-02  wittmer 1355    0   4   1023    \N
838 f   2014-05-02  wittmer 136.218633  0   2   1024    \N
839 f   2014-05-02  wittmer 1362    0   4   1024    \N
840 f   2014-05-02  wittmer 0.750000    0   9   1024    \N
841 f   2014-05-02  wittmer 0.678197    0   10  1024    \N
842 f   2014-05-02  wittmer 1367    0   4   1025    \N
843 f   2014-05-02  wittmer 0.035000    0   9   1025    \N
844 f   2014-05-02  wittmer 0.523729    0   10  1025    \N
845 f   2014-05-02  wittmer 136.918634  0   2   1026    \N
846 f   2014-05-02  wittmer 1369    0   4   1026    \N
847 f   2014-05-02  wittmer 0.150000    0   9   1026    \N
848 f   2014-05-02  wittmer 0.205337    0   10  1026    \N
849 f   2014-05-02  wittmer 137.418389  0   2   1027    \N
850 f   2014-05-02  wittmer 1374    0   4   1027    \N
851 f   2014-05-02  wittmer 0.150000    0   9   1027    \N
852 f   2014-05-02  wittmer 0.205337    0   10  1027    \N
853 f   2014-05-02  wittmer 137.618634  0   2   1028    \N
854 f   2014-05-02  wittmer 1376    0   4   1028    \N
855 f   2014-05-02  wittmer 0.750000    0   9   1028    \N
856 f   2014-05-02  wittmer 0.678197    0   10  1028    \N
857 f   2014-05-02  wittmer 1381    0   4   1029    \N
858 f   2014-05-02  wittmer 0.035000    0   9   1029    \N
859 f   2014-05-02  wittmer 0.523729    0   10  1029    \N
860 f   2014-05-02  wittmer 138.068389  0   2   1030    \N
861 f   2014-05-02  wittmer 1381    0   4   1030    \N
862 f   2014-05-02  wittmer 0.150000    0   9   1030    \N
863 f   2014-05-02  wittmer 0.205337    0   10  1030    \N
864 f   2014-05-02  wittmer 1381    0   4   1031    \N
865 f   2014-05-02  wittmer 0.035000    0   9   1031    \N
866 f   2014-05-02  wittmer 0.523729    0   10  1031    \N
867 f   2014-05-02  wittmer 138.453639  0   2   1032    \N
868 f   2014-05-02  wittmer 1385    0   4   1032    \N
869 f   2014-05-02  wittmer 0.100000    0   9   1032    \N
870 f   2014-05-02  wittmer 0.269567    0   10  1032    \N
871 f   2014-05-02  wittmer 1385    0   4   1033    \N
872 f   2014-05-02  wittmer 0.100000    0   9   1033    \N
873 f   2014-05-02  wittmer 138.683634  0   2   1034    \N
874 f   2014-05-02  wittmer 1387    0   4   1034    \N
875 f   2014-05-02  wittmer 0.150000    0   9   1034    \N
876 f   2014-05-02  wittmer 0.205337    0   10  1034    \N
877 f   2014-05-02  wittmer 138.818639  0   2   1035    \N
878 f   2014-05-02  wittmer 1388    0   4   1035    \N
879 f   2014-05-02  wittmer 0.150000    0   9   1035    \N
880 f   2014-05-02  wittmer 0.205337    0   10  1035    \N
881 f   2014-05-02  wittmer 138.993634  0   2   1036    \N
882 f   2014-05-02  wittmer 1390    0   4   1036    \N
883 f   2014-05-02  wittmer 0.150000    0   9   1036    \N
884 f   2014-05-02  wittmer 0.205337    0   10  1036    \N
885 f   2014-05-02  wittmer 139.353379  0   2   1037    \N
886 f   2014-05-02  wittmer 1393    0   4   1037    \N
887 f   2014-05-02  wittmer 0.100000    0   9   1037    \N
888 f   2014-05-02  wittmer 0.269567    0   10  1037    \N
889 f   2014-05-02  wittmer 1393    0   4   1038    \N
890 f   2014-05-02  wittmer 0.100000    0   9   1038    \N
891 f   2014-05-02  wittmer 139.468639  0   2   1039    \N
892 f   2014-05-02  wittmer 1395    0   4   1039    \N
893 f   2014-05-02  wittmer 0.150000    0   9   1039    \N
894 f   2014-05-02  wittmer 0.205337    0   10  1039    \N
895 f   2014-05-02  wittmer 139.718384  0   2   1040    \N
896 f   2014-05-02  wittmer 1397    0   4   1040    \N
897 f   2014-05-02  wittmer 0.150000    0   9   1040    \N
898 f   2014-05-02  wittmer 0.205337    0   10  1040    \N
899 f   2014-05-02  wittmer 140.397931  0   2   1041    \N
900 f   2014-05-02  wittmer 1404    0   4   1041    \N
901 f   2014-05-02  wittmer 0.100000    0   9   1041    \N
902 f   2014-05-02  wittmer 0.269567    0   10  1041    \N
903 f   2014-05-02  wittmer 1404    0   4   1042    \N
904 f   2014-05-02  wittmer 0.100000    0   9   1042    \N
905 f   2014-05-02  wittmer 1408    0   4   1043    \N
906 f   2014-05-02  wittmer -0.038285   0   10  1043    \N
907 f   2014-05-02  wittmer 140.868393  0   2   1044    \N
908 f   2014-05-02  wittmer 1409    0   4   1044    \N
909 f   2014-05-02  wittmer 0.150000    0   9   1044    \N
910 f   2014-05-02  wittmer 0.205337    0   10  1044    \N
911 f   2014-05-02  wittmer 141.170783  0   2   1045    \N
912 f   2014-05-02  wittmer 1411    0   4   1045    \N
913 f   2014-05-02  wittmer 0.150000    0   9   1045    \N
914 f   2014-05-02  wittmer 0.205337    0   10  1045    \N
915 f   2014-05-02  wittmer 140.978634  0   2   1046    \N
916 f   2014-05-02  wittmer 1413    0   4   1046    \N
917 f   2014-05-02  wittmer 0.100000    0   9   1046    \N
918 f   2014-05-02  wittmer 0.269567    0   10  1046    \N
919 f   2014-05-02  wittmer 1413    0   4   1047    \N
920 f   2014-05-02  wittmer 0.100000    0   9   1047    \N
921 f   2014-05-02  wittmer 141.570787  0   2   1048    \N
922 f   2014-05-02  wittmer 1415    0   4   1048    \N
923 f   2014-05-02  wittmer 0.150000    0   9   1048    \N
924 f   2014-05-02  wittmer 0.205337    0   10  1048    \N
925 f   2014-05-02  wittmer 141.268634  0   2   1049    \N
926 f   2014-05-02  wittmer 1415    0   4   1049    \N
927 f   2014-05-02  wittmer 0.150000    0   9   1049    \N
928 f   2014-05-02  wittmer 0.205337    0   10  1049    \N
929 f   2014-05-02  wittmer 141.945778  0   2   1050    \N
930 f   2014-05-02  wittmer 1420    0   4   1050    \N
931 f   2014-05-02  wittmer 0.032410    0   10  1050    \N
932 f   2014-05-02  wittmer 1420    0   4   1051    \N
933 f   2014-05-02  wittmer -0.038285   0   10  1051    \N
934 f   2014-05-02  wittmer 142.161674  0   2   1052    \N
935 f   2014-05-02  wittmer 1420    0   4   1052    \N
936 f   2014-05-02  wittmer 0.374571    0   10  1052    \N
937 f   2014-05-02  wittmer 1420    0   4   1053    \N
938 f   2014-05-02  wittmer 0.000000    0   10  1053    \N
939 f   2014-05-02  wittmer 1420    0   4   1054    \N
940 f   2014-05-02  wittmer 1422    0   4   1055    \N
941 f   2014-05-02  wittmer 0.035000    0   9   1055    \N
942 f   2014-05-02  wittmer 0.523729    0   10  1055    \N
943 f   2014-05-02  wittmer 142.293381  0   2   1056    \N
944 f   2014-05-02  wittmer 1423    0   4   1056    \N
945 f   2014-05-02  wittmer 0.100000    0   9   1056    \N
946 f   2014-05-02  wittmer 0.269567    0   10  1056    \N
947 f   2014-05-02  wittmer 1423    0   4   1057    \N
948 f   2014-05-02  wittmer 0.100000    0   9   1057    \N
949 f   2014-05-02  wittmer 1427    0   4   1058    \N
950 f   2014-05-02  wittmer -0.038285   0   10  1058    \N
951 f   2014-05-02  wittmer 142.418634  0   2   1059    \N
952 f   2014-05-02  wittmer 1427    0   4   1059    \N
953 f   2014-05-02  wittmer 0.150000    0   9   1059    \N
954 f   2014-05-02  wittmer 0.205337    0   10  1059    \N
955 f   2014-05-02  wittmer 143.068382  0   2   1060    \N
956 f   2014-05-02  wittmer 1431    0   4   1060    \N
957 f   2014-05-02  wittmer 0.150000    0   9   1060    \N
958 f   2014-05-02  wittmer 0.205337    0   10  1060    \N
959 f   2014-05-02  wittmer 142.843634  0   2   1061    \N
960 f   2014-05-02  wittmer 1431    0   4   1061    \N
961 f   2014-05-02  wittmer 0.100000    0   9   1061    \N
962 f   2014-05-02  wittmer 0.269567    0   10  1061    \N
963 f   2014-05-02  wittmer 1431    0   4   1062    \N
964 f   2014-05-02  wittmer 0.100000    0   9   1062    \N
965 f   2014-05-02  wittmer 143.468392  0   2   1063    \N
966 f   2014-05-02  wittmer 1435    0   4   1063    \N
967 f   2014-05-02  wittmer 0.150000    0   9   1063    \N
968 f   2014-05-02  wittmer 0.205337    0   10  1063    \N
969 f   2014-05-02  wittmer 1435    0   4   1064    \N
970 f   2014-05-02  wittmer -0.038285   0   10  1064    \N
971 f   2014-05-02  wittmer 144.093633  0   2   1065    \N
972 f   2014-05-02  wittmer 1439    0   4   1065    \N
973 f   2014-05-02  wittmer 143.843392  0   2   1066    \N
974 f   2014-05-02  wittmer 1439    0   4   1066    \N
975 f   2014-05-02  wittmer 0.032410    0   10  1066    \N
976 f   2014-05-02  wittmer 1439    0   4   1067    \N
977 f   2014-05-02  wittmer -0.038285   0   10  1067    \N
978 f   2014-05-02  wittmer 144.059300  0   2   1068    \N
979 f   2014-05-02  wittmer 1439    0   4   1068    \N
980 f   2014-05-02  wittmer 0.374571    0   10  1068    \N
981 f   2014-05-02  wittmer 1439    0   4   1069    \N
982 f   2014-05-02  wittmer 0.000000    0   10  1069    \N
983 f   2014-05-02  wittmer 1439    0   4   1070    \N
984 f   2014-05-02  wittmer 143.798634  0   2   1071    \N
985 f   2014-05-02  wittmer 1439    0   4   1071    \N
986 f   2014-05-02  wittmer 0.150000    0   9   1071    \N
987 f   2014-05-02  wittmer 0.205337    0   10  1071    \N
988 f   2014-05-02  wittmer 1441    0   4   1072    \N
989 f   2014-05-02  wittmer 0.035000    0   9   1072    \N
990 f   2014-05-02  wittmer 0.523729    0   10  1072    \N
991 f   2014-05-02  wittmer 144.018634  0   2   1073    \N
992 f   2014-05-02  wittmer 1443    0   4   1073    \N
993 f   2014-05-02  wittmer 0.150000    0   9   1073    \N
994 f   2014-05-02  wittmer 0.205337    0   10  1073    \N
995 f   2014-05-02  wittmer 144.393634  0   2   1074    \N
996 f   2014-05-02  wittmer 1448    0   4   1074    \N
997 f   2014-05-02  wittmer 0.032410    0   10  1074    \N
998 f   2014-05-02  wittmer 1448    0   4   1075    \N
999 f   2014-05-02  wittmer -0.038285   0   10  1075    \N
1000    f   2014-05-02  wittmer 144.609534  0   2   1076    \N
1001    f   2014-05-02  wittmer 1448    0   4   1076    \N
1002    f   2014-05-02  wittmer 0.374571    0   10  1076    \N
1003    f   2014-05-02  wittmer 1448    0   4   1077    \N
1004    f   2014-05-02  wittmer 0.000000    0   10  1077    \N
1005    f   2014-05-02  wittmer 1448    0   4   1078    \N
1006    f   2014-05-02  wittmer 144.918398  0   2   1079    \N
1007    f   2014-05-02  wittmer 1450    0   4   1079    \N
1008    f   2014-05-02  wittmer 0.035000    0   9   1079    \N
1009    f   2014-05-02  wittmer 0.523729    0   10  1079    \N
1010    f   2014-05-02  wittmer 1458    0   4   1080    \N
1011    f   2014-05-02  wittmer 145.993633  0   2   1081    \N
1012    f   2014-05-02  wittmer 1460    0   4   1081    \N
1013    f   2014-05-02  wittmer 1460    0   4   1082    \N
1014    f   2014-05-02  wittmer 146.441636  0   2   1083    \N
1015    f   2014-05-02  wittmer 1464    0   4   1083    \N
1016    f   2014-05-02  wittmer 1464    0   4   1084    \N
1017    f   2014-05-02  wittmer 146.543634  0   2   1085    \N
1018    f   2014-05-02  wittmer 1465    0   4   1085    \N
1019    f   2014-05-02  wittmer 147.671504  0   2   1086    \N
1020    f   2014-05-02  wittmer 1477    0   4   1086    \N
1021    f   2014-05-02  wittmer 1477    0   4   1087    \N
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
1   Container Relationship  contained-in    2014-05-15 00:00:00 system  contains    0
2   Power Relationship  powered-by  2014-05-15 00:00:00 system  powers  0
3   Control Relationship    controlled-by   2014-05-15 00:00:00 system  controls    0
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
ampere  \N  electric current    2014-02-25 00:00:00 system  electric current    A   0
ampere-per-meter    \N  magnetic field strength     2014-02-25 00:00:00 system  magnetic field strength     A/m 0
ampere-per-square-meter \N  current density 2014-02-25 00:00:00 system  current density A/m2    0
are \N  1 a = 1 dam2 = 102 m2   2014-02-25 00:00:00 system  area    a   0
astronomical unit   \N  1 ua = 1.495 98 x 1011 m, approximately 2014-02-25 00:00:00 system  length  ua  0
bar \N  1 bar = 0.1 MPa = 100 kPa = 1000 hPa = 105 Pa   2014-02-25 00:00:00 system  pressure    bar 0
barn    \N  1 b = 100 fm2 = 10-28 m2    2014-02-25 00:00:00 system  area    b   0
becquerel   s-1 activity (of a radionuclide)    2014-02-25 00:00:00 system  activity    Bq  0
candela \N  luminous intensity  2014-02-25 00:00:00 system  luminous intensity  cd  0
candela-per-square-meter    \N  luminance   2014-02-25 00:00:00 system  luminance   cd/m2   0
coulomb s·A electric charge, quantity of electricity    2014-02-25 00:00:00 system  electric charge C   0
coulomb-per-cubic-meter \N  electric charge density 2014-02-25 00:00:00 system  electric charge density C/m3    0
coulomb-per-kilogram    \N  exposure (x and  rays)  2014-02-25 00:00:00 system  exposure    C/kg    0
coulomb-per-square-meter    \N  electric flux density   2014-02-25 00:00:00 system  electric flux density   C/m2    0
cubic-meter \N  volume  2014-02-25 00:00:00 system  volume  m3  0
cubic-meter-per-kilogram    \N  specific volume 2014-02-25 00:00:00 system  specific volume m3/kg   0
curie   \N  1 Ci = 3.7 x 1010 Bq    2014-02-25 00:00:00 system  radiation   Ci  0
day \N  1 d = 24 h = 86 400 s   2014-02-25 00:00:00 system  time    d   0
degree (angle)  \N  1° = ( PI/180) rad  2014-02-25 00:00:00 system  angle   deg 0
degree-Celsius  K   Celsius temperature 2014-02-25 00:00:00 system  Celsius temperature °C  0
electronvolt    \N  1 eV = 1.602 18 x 10-19 J, approximately    2014-02-25 00:00:00 system  electric potential difference   eV  0
farad   m-2·kg-1·s4·A2  capacitance 2014-02-25 00:00:00 system  capacitance F   0
farad-per-meter \N  permittivity    2014-02-25 00:00:00 system  permittivity    F/m 0
gray    m2·s-2  absorbed dose, specific energy (imparted), kerma    2014-02-25 00:00:00 system  absorbed dose   Gy  0
gray-per-second \N  absorbed dose rate  2014-02-25 00:00:00 system  absorbed dose rate  Gy/s    0
hectare     \N  1 ha = 1 hm2 = 104 m2   2014-02-25 00:00:00 system  area    ha  0
henry   m2·kg·s-2·A-2   inductance  2014-02-25 00:00:00 system  inductance  H   0
henry-per-meter \N  permeability    2014-02-25 00:00:00 system  permeability    H/m 0
hertz   s-1 frequency   2014-02-25 00:00:00 system  frequency   Hz  0
hour    \N  1 h = 60 min = 3600 s   2014-02-25 00:00:00 system  time    h   0
joule   m2·kg·s-2   energy, work, quantity of heat      2014-02-25 00:00:00 system  energy  J   0
joule-per-cubic-meter   \N  energy density  2014-02-25 00:00:00 system  energy density  J/m3    0
joule-per-kelvin    \N  heat capacity, entropy  2014-02-25 00:00:00 system  entropy J/K 0
joule-per-kilogram  \N  specific energy 2014-02-25 00:00:00 system  specific energy J/kg    0
joule-per-kilogram-kelvin   \N  specific heat capacity, specific entropy    2014-02-25 00:00:00 system  specific heat capacity  J/(kg·K)    0
joule-per-mole  \N  molar energy    2014-02-25 00:00:00 system  molar energy    J/mol   0
joule-per-mole-kelvin   \N  molar entropy, molar heat capacity  2014-02-25 00:00:00 system  molar entropy   J/(mol·K)   0
katal   s-1·mol catalytic activity  2014-02-25 00:00:00 system  catalytic activity  kat 0
katal-per-cubic-meter   \N  catalytic (activity) concentration  2014-02-25 00:00:00 system  catalytic (activity) concentration  kat/m3  0
kelvin  \N  thermodynamic temperature   2014-02-25 00:00:00 system  thermodynamic temperature   K   0
kilogram    \N  mass    2014-02-25 00:00:00 system  mass    kg  0
kilogram-per-cubic-meter    \N  mass density    2014-02-25 00:00:00 system  mass density    kg/m3   0
liter   \N  1 L = 1 dm3 = 10-3 m3   2014-02-25 00:00:00 system  volume  L   0
lumen   cd  luminous flux   2014-02-25 00:00:00 system  luminous flux   lm  0
lux m-2·cd  illuminance 2014-02-25 00:00:00 system  illuminance lx  0
meter   \N  length  2014-02-25 00:00:00 system  length  m   0
meter-per-second    \N  speed, velocity 2014-02-25 00:00:00 system  speed, velocity m/s 0
meter-per-second-squared    \N  acceleration    2014-02-25 00:00:00 system  acceleration    m/s2    0
metric ton  \N  1 t = 103 kg    2014-02-25 00:00:00 system  weight  t   0
minute (angle)  \N  1 minute = (1/60)° = (/10 800) rad  2014-02-25 00:00:00 system  angle   min 0
minute (time)   \N  1 min = 60 s    2014-02-25 00:00:00 system  time    min 0
mole    \N  amount of substance 2014-02-25 00:00:00 system  amount of substance mol 0
mole-per-cubic-meter    \N  amount-of-substance concentration   2014-02-25 00:00:00 system  amount-of-substance mol/m3  0
newton  m·kg·s-2    force   2014-02-25 00:00:00 system  force   N   0
newton-meter    \N  moment of force 2014-02-25 00:00:00 system  moment of force N·m 0
newton-per-meter    \N  surface tension 2014-02-25 00:00:00 system  surface tension N/m 0
pascal  m-1·kg·s-2  pressure, stress    2014-02-25 00:00:00 system  pressure, stress    Pa  0
pascal-second   \N  dynamic viscosity   2014-02-25 00:00:00 system  dynamic viscosity   Pa·s    0
rad \N  1 rad = 1 cGy = 10-2 Gy 2014-02-25 00:00:00 system  radiation   rad 0
radian  \N  plane angle 2014-02-25 00:00:00 system  plane angle rad 0
radian-per-second   \N  angular velocity    2014-02-25 00:00:00 system  angular velocity    rad/s   0
radian-per-second-squared   \N  angular acceleration    2014-02-25 00:00:00 system  angular acceleration    rad/s2  0
reciprocal-meter    \N  wave number 2014-02-25 00:00:00 system  wave number m-1 0
rem \N  1 rem = 1 cSv = 10-2 Sv 2014-02-25 00:00:00 system  radiation   rem 0
roentgen    \N  1 R = 2.58 x 10-4 C/kg  2014-02-25 00:00:00 system  radiation   R   0
second  \N  time    2014-02-25 00:00:00 system  time    s   0
second (angle)  \N  1 second = (1/60) = (/648 000) rad  2014-02-25 00:00:00 system  angle   sec 0
siemens m-2·kg-1·s3·A2  electric conductance    2014-02-25 00:00:00 system  electric conductance    S   0
sievert m2·s-2  dose equivalent (d) 2014-02-25 00:00:00 system  dose equivalent Sv  0
square-meter    \N  area    2014-02-25 00:00:00 system  area    m2  0
steradian   \N  solid angle 2014-02-25 00:00:00 system  solid angle sr  0
tesla   kg·s-2·A-1  magnetic flux density   2014-02-25 00:00:00 system  magnetic flux density   T   0
unified atomic mass unit    \N  1 u = 1.660 54 x 10-27 kg, approximately    2014-02-25 00:00:00 system  mass    u   0
volt    m2·kg·s-3·A-1   electric potential difference,  2014-02-25 00:00:00 system  electric potential difference   V   0
volt-per-meter  \N  electric field strength 2014-02-25 00:00:00 system  electric field strength V/m 0
watt    m2·kg·s-3   power, radiant flux 2014-02-25 00:00:00 system  power, radiant flux W   0
watt-per-meter-kelvin   \N  thermal conductivity    2014-02-25 00:00:00 system  thermal conductivity    W/(m·K) 0
watt-per-square-meter   \N  heat flux density, irradiance   2014-02-25 00:00:00 system  heat flux density   W/m2    0
watt-per-square-meter-steradian \N  radiance    2014-02-25 00:00:00 system  radiance    W/(m2·sr)   0
watt-per-steradian  \N  radiant intensity   2014-02-25 00:00:00 system  radiant intensity   W/sr    0
weber   m2·kg·s-2·A-1   magnetic flux   2014-02-25 00:00:00 system  magnetic flux   Wb  0
ångström    \N  1 Å = 0.1 nm = 10-10 m  2014-02-25 00:00:00 system  length  Å   0
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

