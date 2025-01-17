package com.vitotrips.service;

import com.vitotrips.model.GroupMember;

import java.util.List;

public interface GroupMemberService {
    GroupMember addGroupMember(GroupMember member);
    List<GroupMember> getGroupMembers(Long groupId);
    void removeMember(Long memberId);
}