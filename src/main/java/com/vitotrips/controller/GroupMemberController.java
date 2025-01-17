package com.vitotrips.controller;

import com.vitotrips.model.GroupMember;
import com.vitotrips.service.GroupMemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/groups/members")
public class GroupMemberController {

    private final GroupMemberService groupMemberService;

    public GroupMemberController(GroupMemberService groupMemberService) {
        this.groupMemberService = groupMemberService;
    }

    @PostMapping
    public ResponseEntity<GroupMember> addGroupMember(@RequestBody GroupMember member) {
        return ResponseEntity.ok(groupMemberService.addGroupMember(member));
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<List<GroupMember>> getGroupMembers(@PathVariable Long groupId) {
        return ResponseEntity.ok(groupMemberService.getGroupMembers(groupId));
    }

    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> removeMember(@PathVariable Long memberId) {
        groupMemberService.removeMember(memberId);
        return ResponseEntity.noContent().build();
    }
}