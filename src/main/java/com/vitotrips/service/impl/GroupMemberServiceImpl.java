package com.vitotrips.service.impl;

import com.vitotrips.model.GroupMember;
import com.vitotrips.repository.GroupMemberRepository;
import com.vitotrips.service.GroupMemberService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupMemberServiceImpl implements GroupMemberService {

    private final GroupMemberRepository groupMemberRepository;

    public GroupMemberServiceImpl(GroupMemberRepository groupMemberRepository) {
        this.groupMemberRepository = groupMemberRepository;
    }

    @Override
    public GroupMember addGroupMember(GroupMember member) {
        return groupMemberRepository.save(member);
    }

    @Override
    public List<GroupMember> getGroupMembers(Long groupId) {
        return groupMemberRepository.findByGroup_Id(groupId);
    }

    @Override
    public void removeMember(Long memberId) {
        groupMemberRepository.deleteById(memberId);
    }
}