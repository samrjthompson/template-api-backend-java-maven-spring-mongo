artifact_name	:= application-name
version			:= unversioned

.PHONY: test
test:
	mvn clean
	mvn verify -Dskip.unit.tests=false -Dskip.integration.tests=false