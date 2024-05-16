artifact_name	:= application-name
version			:= unversioned

.PHONY: test
test:
	mvn clean
	mvn verify -Dskip.unit.tests=false -Dskip.integration.tests=false

.PHONY: unit-test
unit-test:
	mvn clean
	mvn verify -Dskip.unit.tests=false -Dskip.integration.tests=true

.PHONY: i-test
i-test:
	mvn clean
	mvn verify -Dskip.unit.tests=true -Dskip.integration.tests=false