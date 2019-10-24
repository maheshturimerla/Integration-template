

def buildProject() {
	stage 'Build Git Project'
	wrap([$class: 'ConfigFileBuildWrapper', managedFiles: [[fileId: 'eb0c7cc1-e851-4bc2-9401-2680c225f88c', targetLocation: '', variable: 'MAVEN_SETTINGS']]]) {
	mvn '-s $MAVEN_SETTINGS -f pom.xml'
}
}

def mvn(args) {
    sh "${tool 'maven3'}/bin/mvn ${args} ${MAVEN_GOALS}"
}

return this