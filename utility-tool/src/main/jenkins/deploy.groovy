def deployService(){
	stage 'Deploying Service'
	
	// get the jenkinsfile root directory 
    def ROOT_DIR = pwd() 
    ROOT_DIR = "${ROOT_DIR}"+'/src/main/kubernetes' 
    echo "ROOTDIR : ${ROOT_DIR}" 
	sh "/opt/app/kubernetes/v1.3.4/bin/kubectl --kubeconfig=${ROOT_DIR}/kubectl.conf replace --force --cascade -f  ${ROOT_DIR}/${artifactId}-svc.yaml" 
	sh "/opt/app/kubernetes/v1.3.4/bin/kubectl --kubeconfig=${ROOT_DIR}/kubectl.conf replace --force --cascade -f  ${ROOT_DIR}/${artifactId}-rc.yaml"
}
return this



 
