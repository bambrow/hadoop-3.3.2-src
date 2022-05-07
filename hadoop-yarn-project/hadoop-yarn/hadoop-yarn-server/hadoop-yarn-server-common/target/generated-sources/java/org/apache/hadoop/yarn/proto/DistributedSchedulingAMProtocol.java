// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: distributed_scheduling_am_protocol.proto

package org.apache.hadoop.yarn.proto;

public final class DistributedSchedulingAMProtocol {
  private DistributedSchedulingAMProtocol() {}
  public static void registerAllExtensions(
      org.apache.hadoop.thirdparty.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      org.apache.hadoop.thirdparty.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (org.apache.hadoop.thirdparty.protobuf.ExtensionRegistryLite) registry);
  }
  /**
   * Protobuf service {@code hadoop.yarn.DistributedSchedulingAMProtocolService}
   */
  public static abstract class DistributedSchedulingAMProtocolService
      implements org.apache.hadoop.thirdparty.protobuf.Service {
    protected DistributedSchedulingAMProtocolService() {}

    public interface Interface {
      /**
       * <code>rpc registerApplicationMasterForDistributedScheduling(.hadoop.yarn.RegisterApplicationMasterRequestProto) returns (.hadoop.yarn.RegisterDistributedSchedulingAMResponseProto);</code>
       */
      public abstract void registerApplicationMasterForDistributedScheduling(
          org.apache.hadoop.thirdparty.protobuf.RpcController controller,
          org.apache.hadoop.yarn.proto.YarnServiceProtos.RegisterApplicationMasterRequestProto request,
          org.apache.hadoop.thirdparty.protobuf.RpcCallback<org.apache.hadoop.yarn.proto.YarnServerCommonServiceProtos.RegisterDistributedSchedulingAMResponseProto> done);

      /**
       * <code>rpc allocateForDistributedScheduling(.hadoop.yarn.DistributedSchedulingAllocateRequestProto) returns (.hadoop.yarn.DistributedSchedulingAllocateResponseProto);</code>
       */
      public abstract void allocateForDistributedScheduling(
          org.apache.hadoop.thirdparty.protobuf.RpcController controller,
          org.apache.hadoop.yarn.proto.YarnServerCommonServiceProtos.DistributedSchedulingAllocateRequestProto request,
          org.apache.hadoop.thirdparty.protobuf.RpcCallback<org.apache.hadoop.yarn.proto.YarnServerCommonServiceProtos.DistributedSchedulingAllocateResponseProto> done);

      /**
       * <code>rpc finishApplicationMaster(.hadoop.yarn.FinishApplicationMasterRequestProto) returns (.hadoop.yarn.FinishApplicationMasterResponseProto);</code>
       */
      public abstract void finishApplicationMaster(
          org.apache.hadoop.thirdparty.protobuf.RpcController controller,
          org.apache.hadoop.yarn.proto.YarnServiceProtos.FinishApplicationMasterRequestProto request,
          org.apache.hadoop.thirdparty.protobuf.RpcCallback<org.apache.hadoop.yarn.proto.YarnServiceProtos.FinishApplicationMasterResponseProto> done);

    }

    public static org.apache.hadoop.thirdparty.protobuf.Service newReflectiveService(
        final Interface impl) {
      return new DistributedSchedulingAMProtocolService() {
        @java.lang.Override
        public  void registerApplicationMasterForDistributedScheduling(
            org.apache.hadoop.thirdparty.protobuf.RpcController controller,
            org.apache.hadoop.yarn.proto.YarnServiceProtos.RegisterApplicationMasterRequestProto request,
            org.apache.hadoop.thirdparty.protobuf.RpcCallback<org.apache.hadoop.yarn.proto.YarnServerCommonServiceProtos.RegisterDistributedSchedulingAMResponseProto> done) {
          impl.registerApplicationMasterForDistributedScheduling(controller, request, done);
        }

        @java.lang.Override
        public  void allocateForDistributedScheduling(
            org.apache.hadoop.thirdparty.protobuf.RpcController controller,
            org.apache.hadoop.yarn.proto.YarnServerCommonServiceProtos.DistributedSchedulingAllocateRequestProto request,
            org.apache.hadoop.thirdparty.protobuf.RpcCallback<org.apache.hadoop.yarn.proto.YarnServerCommonServiceProtos.DistributedSchedulingAllocateResponseProto> done) {
          impl.allocateForDistributedScheduling(controller, request, done);
        }

        @java.lang.Override
        public  void finishApplicationMaster(
            org.apache.hadoop.thirdparty.protobuf.RpcController controller,
            org.apache.hadoop.yarn.proto.YarnServiceProtos.FinishApplicationMasterRequestProto request,
            org.apache.hadoop.thirdparty.protobuf.RpcCallback<org.apache.hadoop.yarn.proto.YarnServiceProtos.FinishApplicationMasterResponseProto> done) {
          impl.finishApplicationMaster(controller, request, done);
        }

      };
    }

    public static org.apache.hadoop.thirdparty.protobuf.BlockingService
        newReflectiveBlockingService(final BlockingInterface impl) {
      return new org.apache.hadoop.thirdparty.protobuf.BlockingService() {
        public final org.apache.hadoop.thirdparty.protobuf.Descriptors.ServiceDescriptor
            getDescriptorForType() {
          return getDescriptor();
        }

        public final org.apache.hadoop.thirdparty.protobuf.Message callBlockingMethod(
            org.apache.hadoop.thirdparty.protobuf.Descriptors.MethodDescriptor method,
            org.apache.hadoop.thirdparty.protobuf.RpcController controller,
            org.apache.hadoop.thirdparty.protobuf.Message request)
            throws org.apache.hadoop.thirdparty.protobuf.ServiceException {
          if (method.getService() != getDescriptor()) {
            throw new java.lang.IllegalArgumentException(
              "Service.callBlockingMethod() given method descriptor for " +
              "wrong service type.");
          }
          switch(method.getIndex()) {
            case 0:
              return impl.registerApplicationMasterForDistributedScheduling(controller, (org.apache.hadoop.yarn.proto.YarnServiceProtos.RegisterApplicationMasterRequestProto)request);
            case 1:
              return impl.allocateForDistributedScheduling(controller, (org.apache.hadoop.yarn.proto.YarnServerCommonServiceProtos.DistributedSchedulingAllocateRequestProto)request);
            case 2:
              return impl.finishApplicationMaster(controller, (org.apache.hadoop.yarn.proto.YarnServiceProtos.FinishApplicationMasterRequestProto)request);
            default:
              throw new java.lang.AssertionError("Can't get here.");
          }
        }

        public final org.apache.hadoop.thirdparty.protobuf.Message
            getRequestPrototype(
            org.apache.hadoop.thirdparty.protobuf.Descriptors.MethodDescriptor method) {
          if (method.getService() != getDescriptor()) {
            throw new java.lang.IllegalArgumentException(
              "Service.getRequestPrototype() given method " +
              "descriptor for wrong service type.");
          }
          switch(method.getIndex()) {
            case 0:
              return org.apache.hadoop.yarn.proto.YarnServiceProtos.RegisterApplicationMasterRequestProto.getDefaultInstance();
            case 1:
              return org.apache.hadoop.yarn.proto.YarnServerCommonServiceProtos.DistributedSchedulingAllocateRequestProto.getDefaultInstance();
            case 2:
              return org.apache.hadoop.yarn.proto.YarnServiceProtos.FinishApplicationMasterRequestProto.getDefaultInstance();
            default:
              throw new java.lang.AssertionError("Can't get here.");
          }
        }

        public final org.apache.hadoop.thirdparty.protobuf.Message
            getResponsePrototype(
            org.apache.hadoop.thirdparty.protobuf.Descriptors.MethodDescriptor method) {
          if (method.getService() != getDescriptor()) {
            throw new java.lang.IllegalArgumentException(
              "Service.getResponsePrototype() given method " +
              "descriptor for wrong service type.");
          }
          switch(method.getIndex()) {
            case 0:
              return org.apache.hadoop.yarn.proto.YarnServerCommonServiceProtos.RegisterDistributedSchedulingAMResponseProto.getDefaultInstance();
            case 1:
              return org.apache.hadoop.yarn.proto.YarnServerCommonServiceProtos.DistributedSchedulingAllocateResponseProto.getDefaultInstance();
            case 2:
              return org.apache.hadoop.yarn.proto.YarnServiceProtos.FinishApplicationMasterResponseProto.getDefaultInstance();
            default:
              throw new java.lang.AssertionError("Can't get here.");
          }
        }

      };
    }

    /**
     * <code>rpc registerApplicationMasterForDistributedScheduling(.hadoop.yarn.RegisterApplicationMasterRequestProto) returns (.hadoop.yarn.RegisterDistributedSchedulingAMResponseProto);</code>
     */
    public abstract void registerApplicationMasterForDistributedScheduling(
        org.apache.hadoop.thirdparty.protobuf.RpcController controller,
        org.apache.hadoop.yarn.proto.YarnServiceProtos.RegisterApplicationMasterRequestProto request,
        org.apache.hadoop.thirdparty.protobuf.RpcCallback<org.apache.hadoop.yarn.proto.YarnServerCommonServiceProtos.RegisterDistributedSchedulingAMResponseProto> done);

    /**
     * <code>rpc allocateForDistributedScheduling(.hadoop.yarn.DistributedSchedulingAllocateRequestProto) returns (.hadoop.yarn.DistributedSchedulingAllocateResponseProto);</code>
     */
    public abstract void allocateForDistributedScheduling(
        org.apache.hadoop.thirdparty.protobuf.RpcController controller,
        org.apache.hadoop.yarn.proto.YarnServerCommonServiceProtos.DistributedSchedulingAllocateRequestProto request,
        org.apache.hadoop.thirdparty.protobuf.RpcCallback<org.apache.hadoop.yarn.proto.YarnServerCommonServiceProtos.DistributedSchedulingAllocateResponseProto> done);

    /**
     * <code>rpc finishApplicationMaster(.hadoop.yarn.FinishApplicationMasterRequestProto) returns (.hadoop.yarn.FinishApplicationMasterResponseProto);</code>
     */
    public abstract void finishApplicationMaster(
        org.apache.hadoop.thirdparty.protobuf.RpcController controller,
        org.apache.hadoop.yarn.proto.YarnServiceProtos.FinishApplicationMasterRequestProto request,
        org.apache.hadoop.thirdparty.protobuf.RpcCallback<org.apache.hadoop.yarn.proto.YarnServiceProtos.FinishApplicationMasterResponseProto> done);

    public static final
        org.apache.hadoop.thirdparty.protobuf.Descriptors.ServiceDescriptor
        getDescriptor() {
      return org.apache.hadoop.yarn.proto.DistributedSchedulingAMProtocol.getDescriptor().getServices().get(0);
    }
    public final org.apache.hadoop.thirdparty.protobuf.Descriptors.ServiceDescriptor
        getDescriptorForType() {
      return getDescriptor();
    }

    public final void callMethod(
        org.apache.hadoop.thirdparty.protobuf.Descriptors.MethodDescriptor method,
        org.apache.hadoop.thirdparty.protobuf.RpcController controller,
        org.apache.hadoop.thirdparty.protobuf.Message request,
        org.apache.hadoop.thirdparty.protobuf.RpcCallback<
          org.apache.hadoop.thirdparty.protobuf.Message> done) {
      if (method.getService() != getDescriptor()) {
        throw new java.lang.IllegalArgumentException(
          "Service.callMethod() given method descriptor for wrong " +
          "service type.");
      }
      switch(method.getIndex()) {
        case 0:
          this.registerApplicationMasterForDistributedScheduling(controller, (org.apache.hadoop.yarn.proto.YarnServiceProtos.RegisterApplicationMasterRequestProto)request,
            org.apache.hadoop.thirdparty.protobuf.RpcUtil.<org.apache.hadoop.yarn.proto.YarnServerCommonServiceProtos.RegisterDistributedSchedulingAMResponseProto>specializeCallback(
              done));
          return;
        case 1:
          this.allocateForDistributedScheduling(controller, (org.apache.hadoop.yarn.proto.YarnServerCommonServiceProtos.DistributedSchedulingAllocateRequestProto)request,
            org.apache.hadoop.thirdparty.protobuf.RpcUtil.<org.apache.hadoop.yarn.proto.YarnServerCommonServiceProtos.DistributedSchedulingAllocateResponseProto>specializeCallback(
              done));
          return;
        case 2:
          this.finishApplicationMaster(controller, (org.apache.hadoop.yarn.proto.YarnServiceProtos.FinishApplicationMasterRequestProto)request,
            org.apache.hadoop.thirdparty.protobuf.RpcUtil.<org.apache.hadoop.yarn.proto.YarnServiceProtos.FinishApplicationMasterResponseProto>specializeCallback(
              done));
          return;
        default:
          throw new java.lang.AssertionError("Can't get here.");
      }
    }

    public final org.apache.hadoop.thirdparty.protobuf.Message
        getRequestPrototype(
        org.apache.hadoop.thirdparty.protobuf.Descriptors.MethodDescriptor method) {
      if (method.getService() != getDescriptor()) {
        throw new java.lang.IllegalArgumentException(
          "Service.getRequestPrototype() given method " +
          "descriptor for wrong service type.");
      }
      switch(method.getIndex()) {
        case 0:
          return org.apache.hadoop.yarn.proto.YarnServiceProtos.RegisterApplicationMasterRequestProto.getDefaultInstance();
        case 1:
          return org.apache.hadoop.yarn.proto.YarnServerCommonServiceProtos.DistributedSchedulingAllocateRequestProto.getDefaultInstance();
        case 2:
          return org.apache.hadoop.yarn.proto.YarnServiceProtos.FinishApplicationMasterRequestProto.getDefaultInstance();
        default:
          throw new java.lang.AssertionError("Can't get here.");
      }
    }

    public final org.apache.hadoop.thirdparty.protobuf.Message
        getResponsePrototype(
        org.apache.hadoop.thirdparty.protobuf.Descriptors.MethodDescriptor method) {
      if (method.getService() != getDescriptor()) {
        throw new java.lang.IllegalArgumentException(
          "Service.getResponsePrototype() given method " +
          "descriptor for wrong service type.");
      }
      switch(method.getIndex()) {
        case 0:
          return org.apache.hadoop.yarn.proto.YarnServerCommonServiceProtos.RegisterDistributedSchedulingAMResponseProto.getDefaultInstance();
        case 1:
          return org.apache.hadoop.yarn.proto.YarnServerCommonServiceProtos.DistributedSchedulingAllocateResponseProto.getDefaultInstance();
        case 2:
          return org.apache.hadoop.yarn.proto.YarnServiceProtos.FinishApplicationMasterResponseProto.getDefaultInstance();
        default:
          throw new java.lang.AssertionError("Can't get here.");
      }
    }

    public static Stub newStub(
        org.apache.hadoop.thirdparty.protobuf.RpcChannel channel) {
      return new Stub(channel);
    }

    public static final class Stub extends org.apache.hadoop.yarn.proto.DistributedSchedulingAMProtocol.DistributedSchedulingAMProtocolService implements Interface {
      private Stub(org.apache.hadoop.thirdparty.protobuf.RpcChannel channel) {
        this.channel = channel;
      }

      private final org.apache.hadoop.thirdparty.protobuf.RpcChannel channel;

      public org.apache.hadoop.thirdparty.protobuf.RpcChannel getChannel() {
        return channel;
      }

      public  void registerApplicationMasterForDistributedScheduling(
          org.apache.hadoop.thirdparty.protobuf.RpcController controller,
          org.apache.hadoop.yarn.proto.YarnServiceProtos.RegisterApplicationMasterRequestProto request,
          org.apache.hadoop.thirdparty.protobuf.RpcCallback<org.apache.hadoop.yarn.proto.YarnServerCommonServiceProtos.RegisterDistributedSchedulingAMResponseProto> done) {
        channel.callMethod(
          getDescriptor().getMethods().get(0),
          controller,
          request,
          org.apache.hadoop.yarn.proto.YarnServerCommonServiceProtos.RegisterDistributedSchedulingAMResponseProto.getDefaultInstance(),
          org.apache.hadoop.thirdparty.protobuf.RpcUtil.generalizeCallback(
            done,
            org.apache.hadoop.yarn.proto.YarnServerCommonServiceProtos.RegisterDistributedSchedulingAMResponseProto.class,
            org.apache.hadoop.yarn.proto.YarnServerCommonServiceProtos.RegisterDistributedSchedulingAMResponseProto.getDefaultInstance()));
      }

      public  void allocateForDistributedScheduling(
          org.apache.hadoop.thirdparty.protobuf.RpcController controller,
          org.apache.hadoop.yarn.proto.YarnServerCommonServiceProtos.DistributedSchedulingAllocateRequestProto request,
          org.apache.hadoop.thirdparty.protobuf.RpcCallback<org.apache.hadoop.yarn.proto.YarnServerCommonServiceProtos.DistributedSchedulingAllocateResponseProto> done) {
        channel.callMethod(
          getDescriptor().getMethods().get(1),
          controller,
          request,
          org.apache.hadoop.yarn.proto.YarnServerCommonServiceProtos.DistributedSchedulingAllocateResponseProto.getDefaultInstance(),
          org.apache.hadoop.thirdparty.protobuf.RpcUtil.generalizeCallback(
            done,
            org.apache.hadoop.yarn.proto.YarnServerCommonServiceProtos.DistributedSchedulingAllocateResponseProto.class,
            org.apache.hadoop.yarn.proto.YarnServerCommonServiceProtos.DistributedSchedulingAllocateResponseProto.getDefaultInstance()));
      }

      public  void finishApplicationMaster(
          org.apache.hadoop.thirdparty.protobuf.RpcController controller,
          org.apache.hadoop.yarn.proto.YarnServiceProtos.FinishApplicationMasterRequestProto request,
          org.apache.hadoop.thirdparty.protobuf.RpcCallback<org.apache.hadoop.yarn.proto.YarnServiceProtos.FinishApplicationMasterResponseProto> done) {
        channel.callMethod(
          getDescriptor().getMethods().get(2),
          controller,
          request,
          org.apache.hadoop.yarn.proto.YarnServiceProtos.FinishApplicationMasterResponseProto.getDefaultInstance(),
          org.apache.hadoop.thirdparty.protobuf.RpcUtil.generalizeCallback(
            done,
            org.apache.hadoop.yarn.proto.YarnServiceProtos.FinishApplicationMasterResponseProto.class,
            org.apache.hadoop.yarn.proto.YarnServiceProtos.FinishApplicationMasterResponseProto.getDefaultInstance()));
      }
    }

    public static BlockingInterface newBlockingStub(
        org.apache.hadoop.thirdparty.protobuf.BlockingRpcChannel channel) {
      return new BlockingStub(channel);
    }

    public interface BlockingInterface {
      public org.apache.hadoop.yarn.proto.YarnServerCommonServiceProtos.RegisterDistributedSchedulingAMResponseProto registerApplicationMasterForDistributedScheduling(
          org.apache.hadoop.thirdparty.protobuf.RpcController controller,
          org.apache.hadoop.yarn.proto.YarnServiceProtos.RegisterApplicationMasterRequestProto request)
          throws org.apache.hadoop.thirdparty.protobuf.ServiceException;

      public org.apache.hadoop.yarn.proto.YarnServerCommonServiceProtos.DistributedSchedulingAllocateResponseProto allocateForDistributedScheduling(
          org.apache.hadoop.thirdparty.protobuf.RpcController controller,
          org.apache.hadoop.yarn.proto.YarnServerCommonServiceProtos.DistributedSchedulingAllocateRequestProto request)
          throws org.apache.hadoop.thirdparty.protobuf.ServiceException;

      public org.apache.hadoop.yarn.proto.YarnServiceProtos.FinishApplicationMasterResponseProto finishApplicationMaster(
          org.apache.hadoop.thirdparty.protobuf.RpcController controller,
          org.apache.hadoop.yarn.proto.YarnServiceProtos.FinishApplicationMasterRequestProto request)
          throws org.apache.hadoop.thirdparty.protobuf.ServiceException;
    }

    private static final class BlockingStub implements BlockingInterface {
      private BlockingStub(org.apache.hadoop.thirdparty.protobuf.BlockingRpcChannel channel) {
        this.channel = channel;
      }

      private final org.apache.hadoop.thirdparty.protobuf.BlockingRpcChannel channel;

      public org.apache.hadoop.yarn.proto.YarnServerCommonServiceProtos.RegisterDistributedSchedulingAMResponseProto registerApplicationMasterForDistributedScheduling(
          org.apache.hadoop.thirdparty.protobuf.RpcController controller,
          org.apache.hadoop.yarn.proto.YarnServiceProtos.RegisterApplicationMasterRequestProto request)
          throws org.apache.hadoop.thirdparty.protobuf.ServiceException {
        return (org.apache.hadoop.yarn.proto.YarnServerCommonServiceProtos.RegisterDistributedSchedulingAMResponseProto) channel.callBlockingMethod(
          getDescriptor().getMethods().get(0),
          controller,
          request,
          org.apache.hadoop.yarn.proto.YarnServerCommonServiceProtos.RegisterDistributedSchedulingAMResponseProto.getDefaultInstance());
      }


      public org.apache.hadoop.yarn.proto.YarnServerCommonServiceProtos.DistributedSchedulingAllocateResponseProto allocateForDistributedScheduling(
          org.apache.hadoop.thirdparty.protobuf.RpcController controller,
          org.apache.hadoop.yarn.proto.YarnServerCommonServiceProtos.DistributedSchedulingAllocateRequestProto request)
          throws org.apache.hadoop.thirdparty.protobuf.ServiceException {
        return (org.apache.hadoop.yarn.proto.YarnServerCommonServiceProtos.DistributedSchedulingAllocateResponseProto) channel.callBlockingMethod(
          getDescriptor().getMethods().get(1),
          controller,
          request,
          org.apache.hadoop.yarn.proto.YarnServerCommonServiceProtos.DistributedSchedulingAllocateResponseProto.getDefaultInstance());
      }


      public org.apache.hadoop.yarn.proto.YarnServiceProtos.FinishApplicationMasterResponseProto finishApplicationMaster(
          org.apache.hadoop.thirdparty.protobuf.RpcController controller,
          org.apache.hadoop.yarn.proto.YarnServiceProtos.FinishApplicationMasterRequestProto request)
          throws org.apache.hadoop.thirdparty.protobuf.ServiceException {
        return (org.apache.hadoop.yarn.proto.YarnServiceProtos.FinishApplicationMasterResponseProto) channel.callBlockingMethod(
          getDescriptor().getMethods().get(2),
          controller,
          request,
          org.apache.hadoop.yarn.proto.YarnServiceProtos.FinishApplicationMasterResponseProto.getDefaultInstance());
      }

    }

    // @@protoc_insertion_point(class_scope:hadoop.yarn.DistributedSchedulingAMProtocolService)
  }


  public static org.apache.hadoop.thirdparty.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  org.apache.hadoop.thirdparty.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n(distributed_scheduling_am_protocol.pro" +
      "to\022\013hadoop.yarn\032\031yarn_service_protos.pro" +
      "to\032\'yarn_server_common_service_protos.pr" +
      "oto2\343\003\n&DistributedSchedulingAMProtocolS" +
      "ervice\022\242\001\n1registerApplicationMasterForD" +
      "istributedScheduling\0222.hadoop.yarn.Regis" +
      "terApplicationMasterRequestProto\0329.hadoo" +
      "p.yarn.RegisterDistributedSchedulingAMRe" +
      "sponseProto\022\223\001\n allocateForDistributedSc" +
      "heduling\0226.hadoop.yarn.DistributedSchedu" +
      "lingAllocateRequestProto\0327.hadoop.yarn.D" +
      "istributedSchedulingAllocateResponseProt" +
      "o\022~\n\027finishApplicationMaster\0220.hadoop.ya" +
      "rn.FinishApplicationMasterRequestProto\0321" +
      ".hadoop.yarn.FinishApplicationMasterResp" +
      "onseProtoBE\n\034org.apache.hadoop.yarn.prot" +
      "oB\037DistributedSchedulingAMProtocol\210\001\001\240\001\001"
    };
    org.apache.hadoop.thirdparty.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
        new org.apache.hadoop.thirdparty.protobuf.Descriptors.FileDescriptor.    InternalDescriptorAssigner() {
          public org.apache.hadoop.thirdparty.protobuf.ExtensionRegistry assignDescriptors(
              org.apache.hadoop.thirdparty.protobuf.Descriptors.FileDescriptor root) {
            descriptor = root;
            return null;
          }
        };
    org.apache.hadoop.thirdparty.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new org.apache.hadoop.thirdparty.protobuf.Descriptors.FileDescriptor[] {
          org.apache.hadoop.yarn.proto.YarnServiceProtos.getDescriptor(),
          org.apache.hadoop.yarn.proto.YarnServerCommonServiceProtos.getDescriptor(),
        }, assigner);
    org.apache.hadoop.yarn.proto.YarnServiceProtos.getDescriptor();
    org.apache.hadoop.yarn.proto.YarnServerCommonServiceProtos.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}