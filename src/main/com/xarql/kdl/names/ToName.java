package com.xarql.kdl.names;

public interface ToName extends ToBaseType {
    InternalName toInternalName();

    InternalObjectName toInternalObjectName();
}
