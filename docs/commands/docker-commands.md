## Docker Compose 명령어

### 빌드 & 실행
```bash
# WSL 진입 후 프로젝트 경로 이동
wsl
cd /mnt/c/APPS/PROJECTS/0_PRACTICE/mycommerce

# 캐시 없이 빌드
docker compose build --no-cache

# 백그라운드 실행
docker compose up -d

# 특정 컨테이너만 실행
docker compose up [컨테이너명] -d
```

### 종료 & 삭제
```bash
# 컨테이너 종료
docker compose down

# 볼륨까지 삭제
docker compose down -v
```

### 정리
```bash
# 사용하지 않는 리소스 삭제
docker system prune -a

# 볼륨까지 삭제
docker system prune -a --volumes

# 특정 이미지 삭제
docker rmi [이미지명]

# 볼륨만 삭제
docker volume prune -a
```

### 확인 & 접속 (WSL)
```bash
# 볼륨 마운트 경로 확인
docker inspect [컨테이너명] --format='{{range .Mounts}}{{.Source}} -> {{.Destination}}{{"\n"}}{{end}}'
#docker inspect db_ecommerce_prod --format='{{range .Mounts}}{{.Source}} -> {{.Destination}}{{"\n"}}{{end}}'
#docker inspect db_ecommerce_prod | grep -A 10 "Mounts"

# 마운트 볼륨 삭제 (mycommerce로 시작하는 모든 볼륨)
docker volume ls
sudo docker volume rm $(sudo docker volume ls -q -f name=mycommerce)

# PostgreSQL 접속
docker exec -it [컨테이너명] psql -U [유저명] -d [DB명]
```