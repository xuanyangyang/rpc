package io.github.xuanyangyang.rpc.core.protocol;

/**
 * 协议管理器
 *
 * @author xuanyangyang
 * @since 2020/10/4 17:13
 */
public interface ProtocolManager {
    /**
     * 通过ID获取协议
     *
     * @param id 协议ID
     * @return 协议
     */
    Protocol getProtocol(Short id);

    /**
     * 添加协议
     *
     * @param protocol 协议
     * @return 是否添加成功
     */
    boolean addProtocol(Protocol protocol);

    /**
     * 删除协议
     *
     * @param id 协议ID
     * @return 被删除的协议
     */
    Protocol removeProtocol(Short id);

    /**
     * @param id 协议ID
     * @return 是否有ID对应的协议
     */
    boolean hasProtocol(Short id);
}
