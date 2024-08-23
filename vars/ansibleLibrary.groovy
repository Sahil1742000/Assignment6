def call(Map config) {
    pipeline {
        agent any
        stages {
            stage('Clone Repository') {
                steps {
                    script {
                        checkout([$class: 'GitSCM',
                            branches: [[name: config.gitBranch]],
                            userRemoteConfigs: [[url: config.gitUrl]]
                        ])
                    }
                }
            }
            stage('User Approval') {
                steps {
                    input message: 'Approve to run Ansible Playbook?', ok: 'Proceed'
                }
            }
            stage('Playbook Execution') {
                steps {
                    script {
                        sh "ansible-playbook ${config.playbookPath} -i ${config.inventory}"
                    }
                }
            }
            stage('Notification') {
                steps {
                    script {
                        // Example: send a notification email
                        emailext body: "Playbook execution completed.",
                                 subject: "Ansible Playbook Status",
                                 to: config.notificationEmail
                    }
                }
            }
        }
    }
}

