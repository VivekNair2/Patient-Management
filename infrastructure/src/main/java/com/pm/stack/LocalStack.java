package com.pm.stack;


import software.amazon.awscdk.services.ec2.InstanceClass;
import software.amazon.awscdk.services.ec2.InstanceSize;
import software.amazon.awscdk.services.ec2.InstanceType;
import software.amazon.awscdk.services.ec2.Vpc;
import software.amazon.awscdk.*;
import software.amazon.awscdk.services.ecs.CloudMapNamespaceOptions;
import software.amazon.awscdk.services.ecs.Cluster;
import software.amazon.awscdk.services.msk.CfnCluster;
import software.amazon.awscdk.services.rds.*;
import software.amazon.awscdk.services.route53.CfnHealthCheck;

public class LocalStack extends Stack {
    private final Vpc vpc;
    private final Cluster ecsCluster;


    public LocalStack(final App scope, final String id, final StackProps props){
        super(scope,id,props);
        this.vpc=createVpc();
        DatabaseInstance authServiceDb=createDatabase("AuthServiceDB","auth-service-db");
        DatabaseInstance patientServiceDb=createDatabase("PatientServiceDB","patient-service-db");
        CfnHealthCheck authDbHealthCheck=createDbHealthCheck(authServiceDb,"AuthDBHealthCheck");
        CfnHealthCheck patientDbHealthCheck=createDbHealthCheck(patientServiceDb,"PatientDBHealthCheck");
        CfnCluster mskCluster = createMskCluster();
        this.ecsCluster=createEcsCluster();
    }
    private Vpc createVpc(){
        return Vpc.Builder.create(this,"PatientManagementVPC").vpcName("PatientManagementVPC").
                maxAzs(2)
                .build();
    }

    private DatabaseInstance createDatabase(String id,String dbName){
        return DatabaseInstance.Builder.create(this,id)
                .engine(DatabaseInstanceEngine.postgres(PostgresInstanceEngineProps
                        .builder().
                        version(PostgresEngineVersion.VER_17_2).build()))
                .vpc(vpc)
                .instanceType(InstanceType.of(InstanceClass.BURSTABLE2, InstanceSize.MICRO))
                .allocatedStorage(20)
                .credentials(Credentials.fromGeneratedSecret("admin_user"))
                .databaseName(dbName)
                .removalPolicy(RemovalPolicy.DESTROY)
                .build();
    }

    private CfnCluster createMskCluster(){
        return CfnCluster.Builder.create(this,"MskCluster")
                .clusterName("kafka-cluster")
                .kafkaVersion("2.8.0")
                .numberOfBrokerNodes(1)
                .brokerNodeGroupInfo(CfnCluster.BrokerNodeGroupInfoProperty.builder()
                .instanceType("kafka.m5.large")
                .clientSubnets(vpc.getPrivateSubnets().stream().map(subnet -> subnet.getSubnetId()).toList())
                        .brokerAzDistribution("DEFAULT")
                .build())
                .build();



    }
    private Cluster createEcsCluster(){
        return Cluster.Builder.create(this,"PatientManagementCluster")
                .vpc(vpc)
                .defaultCloudMapNamespace(CloudMapNamespaceOptions.builder()
                        .name("patient-management-local")
                        .build())
                .build();
    }

    private CfnHealthCheck createDbHealthCheck(DatabaseInstance db,String id){
        return CfnHealthCheck.Builder.create(this,id)
                .healthCheckConfig(CfnHealthCheck.HealthCheckConfigProperty.builder()
                        .type("TCP")
                        .port(Token.asNumber(db.getDbInstanceEndpointAddress()))
                        .requestInterval(30)
                        .failureThreshold(3)
                        .build())
                .build();
    }
    public static void main(final String[] args) {
        App app = new App(AppProps.builder().outdir("./cdk.out").build());
        StackProps props=StackProps.builder().synthesizer(new BootstraplessSynthesizer()).build();
        new LocalStack(app,"Localstack",props);
        app.synth();
        System.out.println("App Synthesizing in progress");

    }
}
