
def gitCheckout() {
	stage 'Checkout GIT'
	//different ways to checkout
	//checkout from master
	//git "url: ${GIT_URL}, branch: ${GIT_BRANCH}"
	//checkout from branch hardcoding"
	//git branch: 'jenkins_deploy_test', credentialsId: 'b9bbafe5-53ce-4d2c-8b84-09137f75c592', url: 'https://codecloud.web.att.com/scm/st_ocnp/sdk-java-starter.git'
	//checkout from branch parameters with credentials
	//git branch: "${GIT_BRANCH}", credentialsId: 'b9bbafe5-53ce-4d2c-8b84-09137f75c592', url: "${GIT_URL}"
	//checkout from branch parameters with no credentials
	git branch: "${GIT_BRANCH}", url: "${GIT_URL}"
}
return this