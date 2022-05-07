// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: server/SCM_Admin_protocol.proto

package org.apache.hadoop.yarn.proto;

public final class SCMAdminProtocol {
  private SCMAdminProtocol() {}
  public static void registerAllExtensions(
      org.apache.hadoop.thirdparty.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      org.apache.hadoop.thirdparty.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (org.apache.hadoop.thirdparty.protobuf.ExtensionRegistryLite) registry);
  }
  /**
   * Protobuf service {@code hadoop.yarn.SCMAdminProtocolService}
   */
  public static abstract class SCMAdminProtocolService
      implements org.apache.hadoop.thirdparty.protobuf.Service {
    protected SCMAdminProtocolService() {}

    public interface Interface {
      /**
       * <code>rpc runCleanerTask(.hadoop.yarn.RunSharedCacheCleanerTaskRequestProto) returns (.hadoop.yarn.RunSharedCacheCleanerTaskResponseProto);</code>
       */
      public abstract void runCleanerTask(
          org.apache.hadoop.thirdparty.protobuf.RpcController controller,
          org.apache.hadoop.yarn.proto.YarnServiceProtos.RunSharedCacheCleanerTaskRequestProto request,
          org.apache.hadoop.thirdparty.protobuf.RpcCallback<org.apache.hadoop.yarn.proto.YarnServiceProtos.RunSharedCacheCleanerTaskResponseProto> done);

    }

    public static org.apache.hadoop.thirdparty.protobuf.Service newReflectiveService(
        final Interface impl) {
      return new SCMAdminProtocolService() {
        @java.lang.Override
        public  void runCleanerTask(
            org.apache.hadoop.thirdparty.protobuf.RpcController controller,
            org.apache.hadoop.yarn.proto.YarnServiceProtos.RunSharedCacheCleanerTaskRequestProto request,
            org.apache.hadoop.thirdparty.protobuf.RpcCallback<org.apache.hadoop.yarn.proto.YarnServiceProtos.RunSharedCacheCleanerTaskResponseProto> done) {
          impl.runCleanerTask(controller, request, done);
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
              return impl.runCleanerTask(controller, (org.apache.hadoop.yarn.proto.YarnServiceProtos.RunSharedCacheCleanerTaskRequestProto)request);
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
              return org.apache.hadoop.yarn.proto.YarnServiceProtos.RunSharedCacheCleanerTaskRequestProto.getDefaultInstance();
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
              return org.apache.hadoop.yarn.proto.YarnServiceProtos.RunSharedCacheCleanerTaskResponseProto.getDefaultInstance();
            default:
              throw new java.lang.AssertionError("Can't get here.");
          }
        }

      };
    }

    /**
     * <code>rpc runCleanerTask(.hadoop.yarn.RunSharedCacheCleanerTaskRequestProto) returns (.hadoop.yarn.RunSharedCacheCleanerTaskResponseProto);</code>
     */
    public abstract void runCleanerTask(
        org.apache.hadoop.thirdparty.protobuf.RpcController controller,
        org.apache.hadoop.yarn.proto.YarnServiceProtos.RunSharedCacheCleanerTaskRequestProto request,
        org.apache.hadoop.thirdparty.protobuf.RpcCallback<org.apache.hadoop.yarn.proto.YarnServiceProtos.RunSharedCacheCleanerTaskResponseProto> done);

    public static final
        org.apache.hadoop.thirdparty.protobuf.Descriptors.ServiceDescriptor
        getDescriptor() {
      return org.apache.hadoop.yarn.proto.SCMAdminProtocol.getDescriptor().getServices().get(0);
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
          this.runCleanerTask(controller, (org.apache.hadoop.yarn.proto.YarnServiceProtos.RunSharedCacheCleanerTaskRequestProto)request,
            org.apache.hadoop.thirdparty.protobuf.RpcUtil.<org.apache.hadoop.yarn.proto.YarnServiceProtos.RunSharedCacheCleanerTaskResponseProto>specializeCallback(
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
          return org.apache.hadoop.yarn.proto.YarnServiceProtos.RunSharedCacheCleanerTaskRequestProto.getDefaultInstance();
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
          return org.apache.hadoop.yarn.proto.YarnServiceProtos.RunSharedCacheCleanerTaskResponseProto.getDefaultInstance();
        default:
          throw new java.lang.AssertionError("Can't get here.");
      }
    }

    public static Stub newStub(
        org.apache.hadoop.thirdparty.protobuf.RpcChannel channel) {
      return new Stub(channel);
    }

    public static final class Stub extends org.apache.hadoop.yarn.proto.SCMAdminProtocol.SCMAdminProtocolService implements Interface {
      private Stub(org.apache.hadoop.thirdparty.protobuf.RpcChannel channel) {
        this.channel = channel;
      }

      private final org.apache.hadoop.thirdparty.protobuf.RpcChannel channel;

      public org.apache.hadoop.thirdparty.protobuf.RpcChannel getChannel() {
        return channel;
      }

      public  void runCleanerTask(
          org.apache.hadoop.thirdparty.protobuf.RpcController controller,
          org.apache.hadoop.yarn.proto.YarnServiceProtos.RunSharedCacheCleanerTaskRequestProto request,
          org.apache.hadoop.thirdparty.protobuf.RpcCallback<org.apache.hadoop.yarn.proto.YarnServiceProtos.RunSharedCacheCleanerTaskResponseProto> done) {
        channel.callMethod(
          getDescriptor().getMethods().get(0),
          controller,
          request,
          org.apache.hadoop.yarn.proto.YarnServiceProtos.RunSharedCacheCleanerTaskResponseProto.getDefaultInstance(),
          org.apache.hadoop.thirdparty.protobuf.RpcUtil.generalizeCallback(
            done,
            org.apache.hadoop.yarn.proto.YarnServiceProtos.RunSharedCacheCleanerTaskResponseProto.class,
            org.apache.hadoop.yarn.proto.YarnServiceProtos.RunSharedCacheCleanerTaskResponseProto.getDefaultInstance()));
      }
    }

    public static BlockingInterface newBlockingStub(
        org.apache.hadoop.thirdparty.protobuf.BlockingRpcChannel channel) {
      return new BlockingStub(channel);
    }

    public interface BlockingInterface {
      public org.apache.hadoop.yarn.proto.YarnServiceProtos.RunSharedCacheCleanerTaskResponseProto runCleanerTask(
          org.apache.hadoop.thirdparty.protobuf.RpcController controller,
          org.apache.hadoop.yarn.proto.YarnServiceProtos.RunSharedCacheCleanerTaskRequestProto request)
          throws org.apache.hadoop.thirdparty.protobuf.ServiceException;
    }

    private static final class BlockingStub implements BlockingInterface {
      private BlockingStub(org.apache.hadoop.thirdparty.protobuf.BlockingRpcChannel channel) {
        this.channel = channel;
      }

      private final org.apache.hadoop.thirdparty.protobuf.BlockingRpcChannel channel;

      public org.apache.hadoop.yarn.proto.YarnServiceProtos.RunSharedCacheCleanerTaskResponseProto runCleanerTask(
          org.apache.hadoop.thirdparty.protobuf.RpcController controller,
          org.apache.hadoop.yarn.proto.YarnServiceProtos.RunSharedCacheCleanerTaskRequestProto request)
          throws org.apache.hadoop.thirdparty.protobuf.ServiceException {
        return (org.apache.hadoop.yarn.proto.YarnServiceProtos.RunSharedCacheCleanerTaskResponseProto) channel.callBlockingMethod(
          getDescriptor().getMethods().get(0),
          controller,
          request,
          org.apache.hadoop.yarn.proto.YarnServiceProtos.RunSharedCacheCleanerTaskResponseProto.getDefaultInstance());
      }

    }

    // @@protoc_insertion_point(class_scope:hadoop.yarn.SCMAdminProtocolService)
  }


  public static org.apache.hadoop.thirdparty.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  org.apache.hadoop.thirdparty.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\037server/SCM_Admin_protocol.proto\022\013hadoo" +
      "p.yarn\032\031yarn_service_protos.proto2\224\001\n\027SC" +
      "MAdminProtocolService\022y\n\016runCleanerTask\022" +
      "2.hadoop.yarn.RunSharedCacheCleanerTaskR" +
      "equestProto\0323.hadoop.yarn.RunSharedCache" +
      "CleanerTaskResponseProtoB6\n\034org.apache.h" +
      "adoop.yarn.protoB\020SCMAdminProtocol\210\001\001\240\001\001"
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