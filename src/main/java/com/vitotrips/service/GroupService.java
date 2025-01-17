package com.vitotrips.service;

import com.vitotrips.model.Group;

import java.util.List;
import java.util.Optional;

public interface GroupService {
    Group saveGroup(Group group);
    List<Group> getAllGroups();
    Optional<Group> getGroupById(Long groupId);
    List<Group> getGroupsByGroupLeader(Long groupLeaderId);
    void deleteGroup(Long groupId);
}