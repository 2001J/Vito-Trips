package com.vitotrips.service.impl;

import com.vitotrips.model.Group;
import com.vitotrips.repository.GroupRepository;
import com.vitotrips.service.GroupService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;

    public GroupServiceImpl(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    @Override
    public Group saveGroup(Group group) {
        return groupRepository.save(group);
    }

    @Override
    public List<Group> getAllGroups() {
        return groupRepository.findAll();
    }

    @Override
    public Optional<Group> getGroupById(Long groupId) {
        return groupRepository.findById(groupId);
    }

    @Override
    public List<Group> getGroupsByGroupLeader(Long groupLeaderId) {
        return groupRepository.findByLeader_Id(groupLeaderId);
    }

    @Override
    public void deleteGroup(Long groupId) {
        groupRepository.deleteById(groupId);
    }
}