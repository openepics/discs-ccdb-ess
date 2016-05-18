The Controls Configuration Database (CCDB) enables the collection, storage, and distribution of
(static) controls configuration data needed to install, commission and operate the European
Spallation Source (ESS) control system.

More specifically, the CCDB manages the information of thousands of (physical and logical) devices
such as cameras, PLCs, IOCs, racks, crates, etc…, that will be in operation at ESS by defining
their properties and relationships between these from the control system perspective.

This information is then consumed both by end-users and other ICS applications 
(e.g. Cable Database, IOC Factory, Csentry) to enable these to successfully perform their domain
specific businesses.


Project organization

The source in the repository code is split into multiple sub-projects. The Maven groupId 
for the project is "org.openepics.discs":
- ccdb-model: the JPA object model (artifactId:ccdb-model)
- ccdb-business: the application business logic; EJBs and supporting classes 
                 (artifactId:ccdb-business)
- ccdb-JAXB: the JAXB classes used with RESTful service and the description of 
                 the service interface (artifactId:ccdb-jaxb)
- ccdb-client-api: the client API other EE applications can use to communicate with 
                 the CCDB REST web service (artifactId:ccdb-client-api)
- conf-core: the user Java EE web application (artifactId:confmgr)
- ccdb-ws: web service implmentation (artifactId:ccdb-ws)

Each module is versioned independently, except the conf-core and ccdb-ws version numbers 
are synced. The ccdb-model, ccdb-business, ccdb-JAXB and ccdb-client-api are all deployed 
into the Maven repository. All sub-projects are tagged independently. The tag naming 
rules are as follows:
- ccdb-model: <version>-model (1.0.2-model)
- ccdb-business: <version>-business (1.3.8-business)
- ccdb-JAXB: <version>-JAXB (1.0.0-JAXB)
- ccdb-client-api: <version>-client-api (1.0.0-client-api)
- ccdb-ws, conf-core: <version> (1.0.4)
