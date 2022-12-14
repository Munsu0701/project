package models.board;

import models.admin.board.AdminBoardDto;
import models.common.BaseDto;
import models.entity.BoardData;
import models.user.UserDto;

public class BoardDataDto extends BaseDto {

	private Long id;
	private String gid;
	private String subject;
	private String poster;
	private String contents;
	private Long hit;
	private boolean isPrivate;
	private String privatePassword;
	private AdminBoardDto board;
	private UserDto user;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getGid() {
		if (gid == null) {
			gid = "" + System.currentTimeMillis();
		}
		return gid;
	}

	public void setGid(String gid) {
		this.gid = gid;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getPoster() {
		return poster;
	}

	public void setPoster(String poster) {
		this.poster = poster;
	}

	public String getContents() {
		return contents;
	}

	public void setContents(String contents) {
		this.contents = contents;
	}

	public Long getHit() {
		if (hit == null) {
			hit = 0L;
		}
		return hit;
	}

	public void setHit(Long hit) {
		this.hit = hit;
	}

	public boolean isPrivate() {
		return isPrivate;
	}

	public void setPrivate(boolean isPrivate) {
		this.isPrivate = isPrivate;
	}
	
	

	public String getPrivatePassword() {
		return privatePassword;
	}

	public void setPrivatePassword(String privatePassword) {
		this.privatePassword = privatePassword;
	}

	public AdminBoardDto getBoard() {
		return board;
	}

	public void setBoard(AdminBoardDto board) {
		this.board = board;
	}

	public UserDto getUser() {
		return user;
	}

	public void setUser(UserDto user) {
		this.user = user;
	}

	@Override
	public String toString() {
		return "BoardDataDto [id=" + id + ", gid=" + gid + ", subject=" + subject + ", poster=" + poster + ", contents="
				+ contents + ", hit=" + hit + ", isPrivate=" + isPrivate + ", privatePassword=" + privatePassword
				+ ", board=" + board + ", user=" + user + "]";
	}

	public static BoardData toEntity(BoardDataDto dto) {
		if (dto == null) {
			return null;
		}
		BoardData entity = new BoardData();

		entity.setGid(dto.getGid());
		entity.setSubject(dto.getSubject());
		entity.setPoster(dto.getPoster());
		entity.setContents(dto.getContents());
		entity.setHit(dto.getHit());
		entity.setPrivate(dto.isPrivate());
		entity.setPrivatePassword(dto.getPrivatePassword());
		entity.setBoard(AdminBoardDto.toEntity(dto.getBoard()));
		entity.setUser(UserDto.toEntity(dto.getUser()));

		return entity;
	}

	public static BoardDataDto toDto(BoardData entity) {
		if (entity == null) {
			return null;
		}
		BoardDataDto dto = new BoardDataDto();

		dto.setId(entity.getId());
		dto.setGid(entity.getGid());
		dto.setSubject(entity.getSubject());
		dto.setPoster(entity.getPoster());
		dto.setContents(entity.getContents());
		dto.setHit(entity.getHit());
		dto.setPrivate(entity.isPrivate());
		dto.setPrivatePassword(entity.getPrivatePassword());
		dto.setBoard(AdminBoardDto.toDto(entity.getBoard()));
		dto.setUser(UserDto.toDto(entity.getUser()));
		dto.setRegDt(entity.getRegDt());
		dto.setModDt(entity.getModDt());

		return dto;
	}
	
	public static BoardDataRequest toRequest(BoardDataDto dto) {
		if (dto == null) {
			return null;
		}
		BoardDataRequest req = new BoardDataRequest();

		req.setId(dto.getId());
		req.setGid(dto.getGid());
		req.setSubject(dto.getSubject());
		req.setPoster(dto.getPoster());
		req.setContents(dto.getContents());
		req.setPrivate(dto.isPrivate());
		req.setPrivatePassword(dto.getPrivatePassword());

		return req;
	}

}
