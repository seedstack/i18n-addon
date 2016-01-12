---
title: "Integration"
addon: "Internationalization"
menu:
    InternationalizationAddon:
        weight: 20
---

If you have already added the `seed-i18n-function` dependency, you just need to configure the function JPA unit and bind
the function roles to yours. This section description how to do it and some other optional configurations.

# Persistence

The i18n function uses JPA as its persistence mechanism. No `persistence.xml` file is provided, as it expects your
application to be configured with [automatically generated persistence information](/docs/seed/manual/persistence/jpa/#without-persistence-xml).
You just need add the the function JPA unit (`seed-i18n-domain`) to the global list of JPA units and specify its datasource:

```ini
[org.seedstack.seed]
persistence.jpa.units = seed-i18n-domain, ...

[org.seedstack.jpa.unit.seed-i18n-domain]
datasource = my-datasource
```

# specify additional properties if needed

# Security

## Simple configuration

All the REST resources allowing to manage the i18n are secured with permissions. These permissions are bound to two
default roles `seed-i18n.reader` and `seed-i18n.translator`. The first one has only read permissions the other has all
the permissions. **Bind the i18n roles to yours.**

Mapping example:

	[org.seedstack.seed.security.users]
	john = password, MYPROJECT.DEVELOPER
	admin = password, MYPROJECT.TRANSLATOR

	[org.seedstack.seed.security.roles]
	seed-i18n.reader=MYPROJECT.DEVELOPER
    seed-i18n.translator=MYPROJECT.TRANSLATOR

## Fine grained permissions

It is possible to create more fine grained security roles using the provided permissions. Here is the list of available
permissions:

	seed:i18n:locale:read
	seed:i18n:locale:write
	seed:i18n:locale:delete

	seed:i18n:key:read
	seed:i18n:key:write
	seed:i18n:key:delete

	seed:i18n:translation:read
	seed:i18n:translation:write
	seed:i18n:translation:delete

# Cache (Optional)

The function uses cache to improve i18n performances. This cache does not need configuration, but it is possible to override default
configuration as follow:

	# Default configuration used by the i18n function
	[org.seedstack.seed.i18n.cache]
	max-size=8192
	concurrency=32
	initial-size=2048

# Backup (Optional)

User interface provides import/export functionality to export keys and translations for all available locales.
But to backup/restore all data with their metadata (e.g. default locale or outdated indicator) the function provides
[shell](#!/seed-doc/shell) commands. To use it, enable the Shell support. Then, use the `core:export` or `core:import`
commands (cf. [Core data documentation](#!/seed-doc/core/data)).