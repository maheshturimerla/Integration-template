#!/bin/bash
#*******************************************************************************
#   BSD License
#    
#   Copyright (c) 2017, AT&T Intellectual Property.  All other rights reserved.
#    
#   Redistribution and use in source and binary forms, with or without modification, are permitted
#   provided that the following conditions are met:
#    
#   1. Redistributions of source code must retain the above copyright notice, this list of conditions
#      and the following disclaimer.
#   2. Redistributions in binary form must reproduce the above copyright notice, this list of
#      conditions and the following disclaimer in the documentation and/or other materials provided
#      with the distribution.
#   3. All advertising materials mentioning features or use of this software must display the
#      following acknowledgement:  This product includes software developed by the AT&T.
#   4. Neither the name of AT&T nor the names of its contributors may be used to endorse or
#      promote products derived from this software without specific prior written permission.
#    
#   THIS SOFTWARE IS PROVIDED BY AT&T INTELLECTUAL PROPERTY ''AS IS'' AND ANY EXPRESS OR
#   IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
#   MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
#   SHALL AT&T INTELLECTUAL PROPERTY BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
#   SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
#   PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;  LOSS OF USE, DATA, OR PROFITS;
#   OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
#   CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
#   ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
#   DAMAGE.
#*******************************************************************************
SCRIPT_DIR=`dirname "${BASH_SOURCE[0]}"`
SCRIPT_DIR=$(cd $SCRIPT_DIR;pwd)

APP_NAME="${APP_NAME:-oppv-widget}"
IMAGE_NAME="${IMAGE_NAME}"
K8S_CTX="${K8S_CTX:-development}"
APP_NS="${APP_NS:-default}"
TARGET_ENV="${TARGET_ENV:-DEV}"
VERSION="${VERSION}"
LABEL_VERSION="${LABEL_VERSION}"
REPLICA_COUNT=${REPLICA_COUNT}
SERVICE_ACCOUNT="${SERVICE_ACCOUNT}"

KUBECTL="${KUBECTL:-kubectl}"
KUBECTL_OPTS="${KUBECTL_OPTS:-}"
#--kubeconfig=${ROOT_DIR}/kubectl.conf

RESOURCES_LIMITS_CPU=${RESOURCES_LIMITS_CPU:-}
RESOURCES_LIMITS_MEMORY=${RESOURCES_LIMITS_MEMORY:-}
RESOURCES_REQUESTS_CPU=${RESOURCES_REQUESTS_CPU:-}
RESOURCES_REQUESTS_MEMORY=${RESOURCES_REQUESTS_MEMORY:-}


function log {
	echo "[$(date +"%F %T")] $*"
}

function generate {
    TEMPLATE_SOURCE=$1
    GENERATED_TARGET=$2

eval "cat <<EOF
$(<${TEMPLATE_SOURCE})
EOF
" > ${GENERATED_TARGET}

    cat ${GENERATED_TARGET}
}


PREFIX=${K8S_CTX}-${TARGET_ENV}-${APP_NS}-${APP_NAME}

generate ${SCRIPT_DIR}/cfgmap.tpl.yml ${SCRIPT_DIR}/.${PREFIX}-cfgmap.yml
generate ${SCRIPT_DIR}/deployment.tpl.yml ${SCRIPT_DIR}/.${PREFIX}-deployment.yml
generate ${SCRIPT_DIR}/svc.tpl.yml ${SCRIPT_DIR}/.${PREFIX}-svc.yml


log "================================================================"
log "Application     : ${APP_NAME}"
log "Deploying Image : ${IMAGE_NAME}"
log "K8S Context     : ${K8S_CTX}"
log "K8S Namespace   : ${APP_NS}"
log "K8S Environment : ${TARGET_ENV}"
log "================================================================"


# configmap
${KUBECTL} apply \
  --namespace ${APP_NS} \
  --context ${K8S_CTX} ${KUBECTL_OPTS} \
  -f ${SCRIPT_DIR}/.${PREFIX}-cfgmap.yml

# svc
${KUBECTL} apply \
  --namespace ${APP_NS} --context ${K8S_CTX} ${KUBECTL_OPTS} \
  -f ${SCRIPT_DIR}/.${PREFIX}-svc.yml

# trigger deployment
${KUBECTL} apply \
  --namespace ${APP_NS} --context ${K8S_CTX} ${KUBECTL_OPTS} \
  -f ${SCRIPT_DIR}/.${PREFIX}-deployment.yml


${KUBECTL} rollout status deployment/${APP_NAME} \
  --namespace ${APP_NS} --context ${K8S_CTX} ${KUBECTL_OPTS}
if [ $? -ne 0 ]; then
    log "================================================================"
    log " Failure - ${APP_NAME} Deployment Failed!!"
    log "================================================================"
    exit 1;
else
    log "================================================================"
    log " Success - ${APP_NAME} Deployment Successfully!"
    log "================================================================"
fi
