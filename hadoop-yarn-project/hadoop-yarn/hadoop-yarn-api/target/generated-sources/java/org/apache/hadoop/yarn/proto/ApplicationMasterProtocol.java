// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: applicationmaster_protocol.proto

package org.apache.hadoop.yarn.proto;

public final class ApplicationMasterProtocol {
  private ApplicationMasterProtocol() {}
  public static void registerAllExtensions(
      org.apache.hadoop.thirdparty.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      org.apache.hadoop.thirdparty.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (org.apache.hadoop.thirdparty.protobuf.ExtensionRegistryLite) registry);
  }
  /**
   * Protobuf service {@code hadoop.yarn.ApplicationMasterProtocolService}
   */
  public static abstract class ApplicationMasterProtocolService
      implements org.apache.hadoop.thirdparty.protobuf.Service {
    protected ApplicationMasterProtocolService() {}

    public interface Interface {
      /**
       * <code>rpc registerApplicationMaster(.hadoop.yarn.RegisterApplicationMasterRequestProto) returns (.hadoop.yarn.RegisterApplicationMasterResponseProto);</code>
       */
      public abstract void registerApplicationMaster(
          org.apache.hadoop.thirdparty.protobuf.RpcController controller,
          org.apache.hadoop.yarn.proto.YarnServiceProtos.RegisterApplicationMasterRequestProto request,
          org.apache.hadoop.thirdparty.protobuf.RpcCallback<org.apache.hadoop.yarn.proto.YarnServiceProtos.RegisterApplicationMasterResponseProto> done);

      /**
       * <code>rpc finishApplicationMaster(.hadoop.yarn.FinishApplicationMasterRequestProto) returns (.hadoop.yarn.FinishApplicationMasterResponseProto);</code>
       */
      public abstract void finishApplicationMaster(
          org.apache.hadoop.thirdparty.protobuf.RpcController controller,
          org.apache.hadoop.yarn.proto.YarnServiceProtos.FinishApplicationMasterRequestProto request,
          org.apache.hadoop.thirdparty.protobuf.RpcCallback<org.apache.hadoop.yarn.proto.YarnServiceProtos.FinishApplicationMasterResponseProto> done);

      /**
       * <code>rpc allocate(.hadoop.yarn.AllocateRequestProto) returns (.hadoop.yarn.AllocateResponseProto);</code>
       */
      public abstract void allocate(
          org.apache.hadoop.thirdparty.protobuf.RpcController controller,
          org.apache.hadoop.yarn.proto.YarnServiceProtos.AllocateRequestProto request,
          org.apache.hadoop.thirdparty.protobuf.RpcCallback<org.apache.hadoop.yarn.proto.YarnServiceProtos.AllocateResponseProto> done);

    }

    public static org.apache.hadoop.thirdparty.protobuf.Service newReflectiveService(
        final Interface impl) {
      return new ApplicationMasterProtocolService() {
        @java.lang.Override
        public  void registerApplicationMaster(
            org.apache.hadoop.thirdparty.protobuf.RpcController controller,
            org.apache.hadoop.yarn.proto.YarnServiceProtos.RegisterApplicationMasterRequestProto request,
            org.apache.hadoop.thirdparty.protobuf.RpcCallback<org.apache.hadoop.yarn.proto.YarnServiceProtos.RegisterApplicationMasterResponseProto> done) {
          impl.registerApplicationMaster(controller, request, done);
        }

        @java.lang.Override
        public  void finishApplicationMaster(
            org.apache.hadoop.thirdparty.protobuf.RpcController controller,
            org.apache.hadoop.yarn.proto.YarnServiceProtos.FinishApplicationMasterRequestProto request,
            org.apache.hadoop.thirdparty.protobuf.RpcCallback<org.apache.hadoop.yarn.proto.YarnServiceProtos.FinishApplicationMasterResponseProto> done) {
          impl.finishApplicationMaster(controller, request, done);
        }

        @java.lang.Override
        public  void allocate(
            org.apache.hadoop.thirdparty.protobuf.RpcController controller,
            org.apache.hadoop.yarn.proto.YarnServiceProtos.AllocateRequestProto request,
            org.apache.hadoop.thirdparty.protobuf.RpcCallback<org.apache.hadoop.yarn.proto.YarnServiceProtos.AllocateResponseProto> done) {
          impl.allocate(controller, request, done);
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
              return impl.registerApplicationMaster(controller, (org.apache.hadoop.yarn.proto.YarnServiceProtos.RegisterApplicationMasterRequestProto)request);
            case 1:
              return impl.finishApplicationMaster(controller, (org.apache.hadoop.yarn.proto.YarnServiceProtos.FinishApplicationMasterRequestProto)request);
            case 2:
              return impl.allocate(controller, (org.apache.hadoop.yarn.proto.YarnServiceProtos.AllocateRequestProto)request);
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
              return org.apache.hadoop.yarn.proto.YarnServiceProtos.FinishApplicationMasterRequestProto.getDefaultInstance();
            case 2:
              return org.apache.hadoop.yarn.proto.YarnServiceProtos.AllocateRequestProto.getDefaultInstance();
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
              return org.apache.hadoop.yarn.proto.YarnServiceProtos.RegisterApplicationMasterResponseProto.getDefaultInstance();
            case 1:
              return org.apache.hadoop.yarn.proto.YarnServiceProtos.FinishApplicationMasterResponseProto.getDefaultInstance();
            case 2:
              return org.apache.hadoop.yarn.proto.YarnServiceProtos.AllocateResponseProto.getDefaultInstance();
            default:
              throw new java.lang.AssertionError("Can't get here.");
          }
        }

      };
    }

    /**
     * <code>rpc registerApplicationMaster(.hadoop.yarn.RegisterApplicationMasterRequestProto) returns (.hadoop.yarn.RegisterApplicationMasterResponseProto);</code>
     */
    public abstract void registerApplicationMaster(
        org.apache.hadoop.thirdparty.protobuf.RpcController controller,
        org.apache.hadoop.yarn.proto.YarnServiceProtos.RegisterApplicationMasterRequestProto request,
        org.apache.hadoop.thirdparty.protobuf.RpcCallback<org.apache.hadoop.yarn.proto.YarnServiceProtos.RegisterApplicationMasterResponseProto> done);

    /**
     * <code>rpc finishApplicationMaster(.hadoop.yarn.FinishApplicationMasterRequestProto) returns (.hadoop.yarn.FinishApplicationMasterResponseProto);</code>
     */
    public abstract void finishApplicationMaster(
        org.apache.hadoop.thirdparty.protobuf.RpcController controller,
        org.apache.hadoop.yarn.proto.YarnServiceProtos.FinishApplicationMasterRequestProto request,
        org.apache.hadoop.thirdparty.protobuf.RpcCallback<org.apache.hadoop.yarn.proto.YarnServiceProtos.FinishApplicationMasterResponseProto> done);

    /**
     * <code>rpc allocate(.hadoop.yarn.AllocateRequestProto) returns (.hadoop.yarn.AllocateResponseProto);</code>
     */
    public abstract void allocate(
        org.apache.hadoop.thirdparty.protobuf.RpcController controller,
        org.apache.hadoop.yarn.proto.YarnServiceProtos.AllocateRequestProto request,
        org.apache.hadoop.thirdparty.protobuf.RpcCallback<org.apache.hadoop.yarn.proto.YarnServiceProtos.AllocateResponseProto> done);

    public static final
        org.apache.hadoop.thirdparty.protobuf.Descriptors.ServiceDescriptor
        getDescriptor() {
      return org.apache.hadoop.yarn.proto.ApplicationMasterProtocol.getDescriptor().getServices().get(0);
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
          this.registerApplicationMaster(controller, (org.apache.hadoop.yarn.proto.YarnServiceProtos.RegisterApplicationMasterRequestProto)request,
            org.apache.hadoop.thirdparty.protobuf.RpcUtil.<org.apache.hadoop.yarn.proto.YarnServiceProtos.RegisterApplicationMasterResponseProto>specializeCallback(
              done));
          return;
        case 1:
          this.finishApplicationMaster(controller, (org.apache.hadoop.yarn.proto.YarnServiceProtos.FinishApplicationMasterRequestProto)request,
            org.apache.hadoop.thirdparty.protobuf.RpcUtil.<org.apache.hadoop.yarn.proto.YarnServiceProtos.FinishApplicationMasterResponseProto>specializeCallback(
              done));
          return;
        case 2:
          this.allocate(controller, (org.apache.hadoop.yarn.proto.YarnServiceProtos.AllocateRequestProto)request,
            org.apache.hadoop.thirdparty.protobuf.RpcUtil.<org.apache.hadoop.yarn.proto.YarnServiceProtos.AllocateResponseProto>specializeCallback(
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
          return org.apache.hadoop.yarn.proto.YarnServiceProtos.FinishApplicationMasterRequestProto.getDefaultInstance();
        case 2:
          return org.apache.hadoop.yarn.proto.YarnServiceProtos.AllocateRequestProto.getDefaultInstance();
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
          return org.apache.hadoop.yarn.proto.YarnServiceProtos.RegisterApplicationMasterResponseProto.getDefaultInstance();
        case 1:
          return org.apache.hadoop.yarn.proto.YarnServiceProtos.FinishApplicationMasterResponseProto.getDefaultInstance();
        case 2:
          return org.apache.hadoop.yarn.proto.YarnServiceProtos.AllocateResponseProto.getDefaultInstance();
        default:
          throw new java.lang.AssertionError("Can't get here.");
      }
    }

    public static Stub newStub(
        org.apache.hadoop.thirdparty.protobuf.RpcChannel channel) {
      return new Stub(channel);
    }

    public static final class Stub extends org.apache.hadoop.yarn.proto.ApplicationMasterProtocol.ApplicationMasterProtocolService implements Interface {
      private Stub(org.apache.hadoop.thirdparty.protobuf.RpcChannel channel) {
        this.channel = channel;
      }

      private final org.apache.hadoop.thirdparty.protobuf.RpcChannel channel;

      public org.apache.hadoop.thirdparty.protobuf.RpcChannel getChannel() {
        return channel;
      }

      public  void registerApplicationMaster(
          org.apache.hadoop.thirdparty.protobuf.RpcController controller,
          org.apache.hadoop.yarn.proto.YarnServiceProtos.RegisterApplicationMasterRequestProto request,
          org.apache.hadoop.thirdparty.protobuf.RpcCallback<org.apache.hadoop.yarn.proto.YarnServiceProtos.RegisterApplicationMasterResponseProto> done) {
        channel.callMethod(
          getDescriptor().getMethods().get(0),
          controller,
          request,
          org.apache.hadoop.yarn.proto.YarnServiceProtos.RegisterApplicationMasterResponseProto.getDefaultInstance(),
          org.apache.hadoop.thirdparty.protobuf.RpcUtil.generalizeCallback(
            done,
            org.apache.hadoop.yarn.proto.YarnServiceProtos.RegisterApplicationMasterResponseProto.class,
            org.apache.hadoop.yarn.proto.YarnServiceProtos.RegisterApplicationMasterResponseProto.getDefaultInstance()));
      }

      public  void finishApplicationMaster(
          org.apache.hadoop.thirdparty.protobuf.RpcController controller,
          org.apache.hadoop.yarn.proto.YarnServiceProtos.FinishApplicationMasterRequestProto request,
          org.apache.hadoop.thirdparty.protobuf.RpcCallback<org.apache.hadoop.yarn.proto.YarnServiceProtos.FinishApplicationMasterResponseProto> done) {
        channel.callMethod(
          getDescriptor().getMethods().get(1),
          controller,
          request,
          org.apache.hadoop.yarn.proto.YarnServiceProtos.FinishApplicationMasterResponseProto.getDefaultInstance(),
          org.apache.hadoop.thirdparty.protobuf.RpcUtil.generalizeCallback(
            done,
            org.apache.hadoop.yarn.proto.YarnServiceProtos.FinishApplicationMasterResponseProto.class,
            org.apache.hadoop.yarn.proto.YarnServiceProtos.FinishApplicationMasterResponseProto.getDefaultInstance()));
      }

      public  void allocate(
          org.apache.hadoop.thirdparty.protobuf.RpcController controller,
          org.apache.hadoop.yarn.proto.YarnServiceProtos.AllocateRequestProto request,
          org.apache.hadoop.thirdparty.protobuf.RpcCallback<org.apache.hadoop.yarn.proto.YarnServiceProtos.AllocateResponseProto> done) {
        channel.callMethod(
          getDescriptor().getMethods().get(2),
          controller,
          request,
          org.apache.hadoop.yarn.proto.YarnServiceProtos.AllocateResponseProto.getDefaultInstance(),
          org.apache.hadoop.thirdparty.protobuf.RpcUtil.generalizeCallback(
            done,
            org.apache.hadoop.yarn.proto.YarnServiceProtos.AllocateResponseProto.class,
            org.apache.hadoop.yarn.proto.YarnServiceProtos.AllocateResponseProto.getDefaultInstance()));
      }
    }

    public static BlockingInterface newBlockingStub(
        org.apache.hadoop.thirdparty.protobuf.BlockingRpcChannel channel) {
      return new BlockingStub(channel);
    }

    public interface BlockingInterface {
      public org.apache.hadoop.yarn.proto.YarnServiceProtos.RegisterApplicationMasterResponseProto registerApplicationMaster(
          org.apache.hadoop.thirdparty.protobuf.RpcController controller,
          org.apache.hadoop.yarn.proto.YarnServiceProtos.RegisterApplicationMasterRequestProto request)
          throws org.apache.hadoop.thirdparty.protobuf.ServiceException;

      public org.apache.hadoop.yarn.proto.YarnServiceProtos.FinishApplicationMasterResponseProto finishApplicationMaster(
          org.apache.hadoop.thirdparty.protobuf.RpcController controller,
          org.apache.hadoop.yarn.proto.YarnServiceProtos.FinishApplicationMasterRequestProto request)
          throws org.apache.hadoop.thirdparty.protobuf.ServiceException;

      public org.apache.hadoop.yarn.proto.YarnServiceProtos.AllocateResponseProto allocate(
          org.apache.hadoop.thirdparty.protobuf.RpcController controller,
          org.apache.hadoop.yarn.proto.YarnServiceProtos.AllocateRequestProto request)
          throws org.apache.hadoop.thirdparty.protobuf.ServiceException;
    }

    private static final class BlockingStub implements BlockingInterface {
      private BlockingStub(org.apache.hadoop.thirdparty.protobuf.BlockingRpcChannel channel) {
        this.channel = channel;
      }

      private final org.apache.hadoop.thirdparty.protobuf.BlockingRpcChannel channel;

      public org.apache.hadoop.yarn.proto.YarnServiceProtos.RegisterApplicationMasterResponseProto registerApplicationMaster(
          org.apache.hadoop.thirdparty.protobuf.RpcController controller,
          org.apache.hadoop.yarn.proto.YarnServiceProtos.RegisterApplicationMasterRequestProto request)
          throws org.apache.hadoop.thirdparty.protobuf.ServiceException {
        return (org.apache.hadoop.yarn.proto.YarnServiceProtos.RegisterApplicationMasterResponseProto) channel.callBlockingMethod(
          getDescriptor().getMethods().get(0),
          controller,
          request,
          org.apache.hadoop.yarn.proto.YarnServiceProtos.RegisterApplicationMasterResponseProto.getDefaultInstance());
      }


      public org.apache.hadoop.yarn.proto.YarnServiceProtos.FinishApplicationMasterResponseProto finishApplicationMaster(
          org.apache.hadoop.thirdparty.protobuf.RpcController controller,
          org.apache.hadoop.yarn.proto.YarnServiceProtos.FinishApplicationMasterRequestProto request)
          throws org.apache.hadoop.thirdparty.protobuf.ServiceException {
        return (org.apache.hadoop.yarn.proto.YarnServiceProtos.FinishApplicationMasterResponseProto) channel.callBlockingMethod(
          getDescriptor().getMethods().get(1),
          controller,
          request,
          org.apache.hadoop.yarn.proto.YarnServiceProtos.FinishApplicationMasterResponseProto.getDefaultInstance());
      }


      public org.apache.hadoop.yarn.proto.YarnServiceProtos.AllocateResponseProto allocate(
          org.apache.hadoop.thirdparty.protobuf.RpcController controller,
          org.apache.hadoop.yarn.proto.YarnServiceProtos.AllocateRequestProto request)
          throws org.apache.hadoop.thirdparty.protobuf.ServiceException {
        return (org.apache.hadoop.yarn.proto.YarnServiceProtos.AllocateResponseProto) channel.callBlockingMethod(
          getDescriptor().getMethods().get(2),
          controller,
          request,
          org.apache.hadoop.yarn.proto.YarnServiceProtos.AllocateResponseProto.getDefaultInstance());
      }

    }

    // @@protoc_insertion_point(class_scope:hadoop.yarn.ApplicationMasterProtocolService)
  }


  public static org.apache.hadoop.thirdparty.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  org.apache.hadoop.thirdparty.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n applicationmaster_protocol.proto\022\013hado" +
      "op.yarn\032\031yarn_service_protos.proto2\374\002\n A" +
      "pplicationMasterProtocolService\022\204\001\n\031regi" +
      "sterApplicationMaster\0222.hadoop.yarn.Regi" +
      "sterApplicationMasterRequestProto\0323.hado" +
      "op.yarn.RegisterApplicationMasterRespons" +
      "eProto\022~\n\027finishApplicationMaster\0220.hado" +
      "op.yarn.FinishApplicationMasterRequestPr" +
      "oto\0321.hadoop.yarn.FinishApplicationMaste" +
      "rResponseProto\022Q\n\010allocate\022!.hadoop.yarn" +
      ".AllocateRequestProto\032\".hadoop.yarn.Allo" +
      "cateResponseProtoB?\n\034org.apache.hadoop.y" +
      "arn.protoB\031ApplicationMasterProtocol\210\001\001\240" +
      "\001\001"
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
        }, assigner);
    org.apache.hadoop.yarn.proto.YarnServiceProtos.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}
